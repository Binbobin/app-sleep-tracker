package com.example.trakingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trakingapp.alarm.AlarmSoundManager
import com.example.trakingapp.data.model.EnvironmentRating
import com.example.trakingapp.data.model.SleepSession
import com.example.trakingapp.data.repository.SleepRepository
import com.example.trakingapp.data.repository.SleepSummaryStats
import com.example.trakingapp.sensor.LuxCategory
import com.example.trakingapp.sensor.SleepSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SleepUiState(
    val isTracking: Boolean = false,
    val startTime: Long = 0L,
    val elapsedSeconds: Long = 0L,
    val targetDurationSeconds: Long = 450 * 60L,
    val remainingSeconds: Long = 450 * 60L,
    val targetWakeTimeMillis: Long = 0L,
    val targetDurationMinutes: Int = 450,
    val targetHour: Int = 6,
    val targetMinute: Int = 30,
    val isAlarmRinging: Boolean = false,
    val snoozeUntilMillis: Long = 0L,
    val snoozeCount: Int = 0,
    val currentLux: Float = 0f,
    val luxCategory: LuxCategory = LuxCategory.PITCH_DARK,
    val avgLux: Float = 0f,
    val maxLux: Float = 0f,
    val disturbanceCount: Int = 0,
    val lastDisturbanceTime: String = "",
    val isFaceDown: Boolean = false,
    val snoozeBannerMessage: String? = null,
    val completedSessionSummary: SleepSession? = null,
    val historyList: List<SleepSession> = emptyList(),
    val summaryStats: SleepSummaryStats = SleepSummaryStats(),
    val hasHardwareLightSensor: Boolean = true,
    val hasHardwareAccelerometer: Boolean = true
) {
    val formattedTargetTime: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", targetHour, targetMinute)

    val formattedElapsedTime: String
        get() {
            val hours = elapsedSeconds / 3600
            val minutes = (elapsedSeconds % 3600) / 60
            val seconds = elapsedSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        }

    val formattedRemainingTime: String
        get() {
            val safeSec = remainingSeconds.coerceAtLeast(0)
            val hours = safeSec / 3600
            val minutes = (safeSec % 3600) / 60
            val seconds = safeSec % 60
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        }

    val sleepProgress: Float
        get() {
            if (targetDurationSeconds <= 0L) return 0f
            return (elapsedSeconds.toFloat() / targetDurationSeconds.toFloat()).coerceIn(0f, 1f)
        }

    val formattedTargetDuration: String
        get() {
            val hours = targetDurationMinutes / 60
            val minutes = targetDurationMinutes % 60
            return if (hours > 0) "${hours}j ${minutes}m" else "${minutes}m"
        }
}

class SleepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SleepRepository(application)
    private val alarmManager = AlarmSoundManager(application)

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var alarmTriggeredForSession = false

    private val sensorManager = SleepSensorManager(
        context = application,
        onSensorUpdate = { curLux, avgLux, disturbances, faceDown ->
            _uiState.update { state ->
                state.copy(
                    currentLux = curLux,
                    luxCategory = LuxCategory.fromLux(curLux),
                    avgLux = avgLux,
                    disturbanceCount = disturbances,
                    isFaceDown = faceDown
                )
            }
        },
        onDisturbance = {
            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            _uiState.update { state ->
                state.copy(
                    lastDisturbanceTime = timeStr,
                    disturbanceCount = state.disturbanceCount + 1
                )
            }
        },
        onFlipToSnooze = {
            snoozeAlarmByFlip()
        }
    )

    init {
        _uiState.update {
            it.copy(
                hasHardwareLightSensor = sensorManager.hasLightSensor,
                hasHardwareAccelerometer = sensorManager.hasAccelerometer
            )
        }
        loadHistory()
    }

    fun setWakeTime(hour: Int, minute: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val diffMinutes = ((target.timeInMillis - now.timeInMillis) / (60 * 1000)).toInt().coerceAtLeast(1)

        _uiState.update {
            it.copy(
                targetHour = hour,
                targetMinute = minute,
                targetDurationMinutes = diffMinutes,
                targetDurationSeconds = diffMinutes * 60L,
                targetWakeTimeMillis = target.timeInMillis
            )
        }
    }

    fun setTargetDuration(durationMinutes: Int) {
        val now = System.currentTimeMillis()
        val targetWake = now + (durationMinutes * 60 * 1000L)
        val cal = Calendar.getInstance().apply { timeInMillis = targetWake }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        _uiState.update {
            it.copy(
                targetDurationMinutes = durationMinutes,
                targetDurationSeconds = durationMinutes * 60L,
                targetWakeTimeMillis = targetWake,
                targetHour = hour,
                targetMinute = minute
            )
        }
    }

    fun startSleepTracking(durationMinutes: Int = _uiState.value.targetDurationMinutes) {
        val now = System.currentTimeMillis()
        val totalSec = durationMinutes * 60L
        val targetWakeMillis = now + (totalSec * 1000L)

        val cal = Calendar.getInstance().apply { timeInMillis = targetWakeMillis }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        alarmTriggeredForSession = false

        sensorManager.startTracking()
        sensorManager.isAlarmActive = false

        _uiState.update {
            it.copy(
                isTracking = true,
                startTime = now,
                targetDurationMinutes = durationMinutes,
                targetDurationSeconds = totalSec,
                remainingSeconds = totalSec,
                targetWakeTimeMillis = targetWakeMillis,
                targetHour = hour,
                targetMinute = minute,
                elapsedSeconds = 0L,
                isAlarmRinging = false,
                snoozeUntilMillis = 0L,
                snoozeCount = 0,
                currentLux = 0f,
                avgLux = 0f,
                maxLux = 0f,
                disturbanceCount = 0,
                lastDisturbanceTime = "",
                snoozeBannerMessage = null,
                completedSessionSummary = null
            )
        }

        startTimer()
    }

    fun stopSleepTracking() {
        if (!_uiState.value.isTracking) return

        sensorManager.stopTracking()
        alarmManager.stopAlarm()
        sensorManager.isAlarmActive = false
        timerJob?.cancel()

        val endTime = System.currentTimeMillis()
        val currentState = _uiState.value

        val rating = EnvironmentRating.calculate(
            avgLux = currentState.avgLux,
            disturbanceCount = currentState.disturbanceCount
        )

        val summary = SleepSession(
            startTime = currentState.startTime,
            endTime = endTime,
            targetWakeTime = "${currentState.formattedTargetTime} (Target ${currentState.formattedTargetDuration})",
            avgLux = currentState.avgLux,
            maxLux = currentState.maxLux,
            disturbanceCount = currentState.disturbanceCount,
            environmentRating = rating.title,
            notes = rating.description
        )

        // Automatically save to SQLite database
        viewModelScope.launch {
            repository.saveSession(summary)
            loadHistory()
        }

        _uiState.update {
            it.copy(
                isTracking = false,
                isAlarmRinging = false,
                completedSessionSummary = summary
            )
        }
    }

    fun snoozeAlarm() {
        val snoozeDurationMs = 5 * 60 * 1000L // 5 minutes
        val snoozeUntil = System.currentTimeMillis() + snoozeDurationMs

        alarmManager.stopAlarm()
        sensorManager.isAlarmActive = false

        _uiState.update {
            it.copy(
                isAlarmRinging = false,
                snoozeUntilMillis = snoozeUntil,
                remainingSeconds = 5 * 60L,
                snoozeCount = it.snoozeCount + 1,
                snoozeBannerMessage = "Alarm ditunda (Snooze) selama 5 menit"
            )
        }

        // Hide banner after 4 seconds
        viewModelScope.launch {
            delay(4000)
            _uiState.update { it.copy(snoozeBannerMessage = null) }
        }
    }

    private fun snoozeAlarmByFlip() {
        val snoozeDurationMs = 5 * 60 * 1000L
        val snoozeUntil = System.currentTimeMillis() + snoozeDurationMs

        alarmManager.stopAlarm()
        sensorManager.isAlarmActive = false

        _uiState.update {
            it.copy(
                isAlarmRinging = false,
                snoozeUntilMillis = snoozeUntil,
                remainingSeconds = 5 * 60L,
                snoozeCount = it.snoozeCount + 1,
                snoozeBannerMessage = "Gerakan Flip terdeteksi! Alarm ditunda 5 menit."
            )
        }

        viewModelScope.launch {
            delay(4000)
            _uiState.update { it.copy(snoozeBannerMessage = null) }
        }
    }

    fun dismissAlarm() {
        alarmManager.stopAlarm()
        sensorManager.isAlarmActive = false
        alarmTriggeredForSession = true

        _uiState.update {
            it.copy(
                isAlarmRinging = false,
                snoozeUntilMillis = 0L,
                snoozeBannerMessage = "Alarm dimatikan. Selamat pagi!"
            )
        }

        viewModelScope.launch {
            delay(3500)
            _uiState.update { it.copy(snoozeBannerMessage = null) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val currentState = _uiState.value
                val now = System.currentTimeMillis()
                val elapsed = (now - currentState.startTime) / 1000
                val remaining = if (currentState.targetWakeTimeMillis > 0L) {
                    ((currentState.targetWakeTimeMillis - now) / 1000).coerceAtLeast(0)
                } else {
                    (currentState.targetDurationSeconds - elapsed).coerceAtLeast(0)
                }

                _uiState.update {
                    it.copy(
                        elapsedSeconds = elapsed,
                        remainingSeconds = remaining
                    )
                }

                checkAlarmTrigger()
            }
        }
    }

    private fun checkAlarmTrigger() {
        val state = _uiState.value
        if (!state.isTracking || state.isAlarmRinging) return

        val currentTimeMillis = System.currentTimeMillis()

        // 1. Check if snooze expired
        if (state.snoozeUntilMillis > 0L && currentTimeMillis >= state.snoozeUntilMillis) {
            triggerAlarm()
            return
        }

        // 2. Check if countdown expired or target wake time reached
        if (!alarmTriggeredForSession && state.snoozeUntilMillis == 0L) {
            if (state.targetWakeTimeMillis > 0L && currentTimeMillis >= state.targetWakeTimeMillis) {
                alarmTriggeredForSession = true
                triggerAlarm()
            }
        }
    }

    fun triggerAlarmManuallyForTesting() {
        triggerAlarm()
    }

    private fun triggerAlarm() {
        alarmManager.startAlarm()
        sensorManager.isAlarmActive = true

        _uiState.update {
            it.copy(
                isAlarmRinging = true,
                snoozeUntilMillis = 0L
            )
        }
    }

    fun dismissSummaryDialog() {
        _uiState.update { it.copy(completedSessionSummary = null) }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val list = repository.getAllSessions()
            val stats = repository.getSummaryStats()
            _uiState.update {
                it.copy(historyList = list, summaryStats = stats)
            }
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.deleteSession(id)
            loadHistory()
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            loadHistory()
        }
    }

    // Testing simulation functions (vital for presentations & testing on devices/emulators)
    fun simulateDisturbance() {
        if (_uiState.value.isTracking) {
            sensorManager.simulateDisturbance()
        }
    }

    fun simulateLuxReading(lux: Float) {
        if (_uiState.value.isTracking) {
            sensorManager.simulateLuxReading(lux)
        }
    }

    fun simulateFlipToSnooze() {
        if (_uiState.value.isAlarmRinging) {
            sensorManager.simulateFlip()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopTracking()
        alarmManager.stopAlarm()
        timerJob?.cancel()
    }
}
