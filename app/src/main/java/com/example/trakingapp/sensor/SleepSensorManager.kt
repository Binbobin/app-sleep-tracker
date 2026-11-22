package com.example.trakingapp.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

enum class LuxCategory(val label: String, val description: String) {
    PITCH_DARK("Pitch Dark", "Optimal untuk produksi melatonin alami"),
    DIM("Dim", "Pencahayaan redup, cukup aman untuk tidur"),
    TOO_BRIGHT("Terlalu Terang", "Cahaya dapat mengganggu fase tidur nyenyak");

    companion object {
        fun fromLux(lux: Float): LuxCategory {
            return when {
                lux < 2.0f -> PITCH_DARK
                lux < 15.0f -> DIM
                else -> TOO_BRIGHT
            }
        }
    }
}

class SleepSensorManager(
    context: Context,
    private val onSensorUpdate: (currentLux: Float, avgLux: Float, disturbanceCount: Int, isFaceDown: Boolean) -> Unit,
    private val onDisturbance: () -> Unit,
    private val onFlipToSnooze: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var isTracking = false
        private set

    var isAlarmActive = false

    // Light Sensor tracking
    private var luxSum = 0.0
    private var luxSamplesCount = 0
    var currentLux = 0f
        private set
    var avgLux = 0f
        private set
    var maxLux = 0f
        private set

    // Motion Sensor tracking
    var disturbanceCount = 0
        private set
    private var lastDisturbanceTime = 0L
    private val disturbanceThreshold = 2.0f // Jerk magnitude above gravity
    private val disturbanceCooldown = 1500L // 1.5 seconds cooldown

    // Flip detection
    private var wasFaceUp = true
    var isFaceDown = false
        private set

    val hasLightSensor: Boolean
        get() = lightSensor != null

    val hasAccelerometer: Boolean
        get() = accelerometer != null

    fun startTracking() {
        if (isTracking) return
        isTracking = true
        resetMetrics()

        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTracking() {
        if (!isTracking) return
        isTracking = false
        sensorManager.unregisterListener(this)
    }

    fun resetMetrics() {
        luxSum = 0.0
        luxSamplesCount = 0
        currentLux = 0f
        avgLux = 0f
        maxLux = 0f
        disturbanceCount = 0
        lastDisturbanceTime = 0L
        wasFaceUp = true
        isFaceDown = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isTracking) return

        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                val lux = event.values[0]
                currentLux = lux
                luxSum += lux
                luxSamplesCount++
                avgLux = (luxSum / luxSamplesCount).toFloat()
                if (lux > maxLux) maxLux = lux

                onSensorUpdate(currentLux, avgLux, disturbanceCount, isFaceDown)
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val currentTime = System.currentTimeMillis()

                // 1. Motion disturbance calculation
                val totalAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val deltaFromGravity = Math.abs(totalAcceleration - SensorManager.GRAVITY_EARTH)

                if (deltaFromGravity > disturbanceThreshold) {
                    if (currentTime - lastDisturbanceTime > disturbanceCooldown) {
                        lastDisturbanceTime = currentTime
                        disturbanceCount++
                        onDisturbance()
                    }
                }

                // 2. Flip-to-snooze detection
                // When phone is face-up on table, z is ~ +9.8
                // When phone is flipped face-down, z is ~ -9.8
                val currentFaceDown = z < -7.0f
                isFaceDown = currentFaceDown

                if (!wasFaceUp && z > 5.0f) {
                    wasFaceUp = true
                }

                if (isAlarmActive && wasFaceUp && currentFaceDown) {
                    wasFaceUp = false
                    onFlipToSnooze()
                }

                onSensorUpdate(currentLux, avgLux, disturbanceCount, isFaceDown)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed for accuracy adjustments
    }

    // Manual test triggers for testing or emulators without hardware sensors
    fun simulateDisturbance() {
        disturbanceCount++
        onDisturbance()
        onSensorUpdate(currentLux, avgLux, disturbanceCount, isFaceDown)
    }

    fun simulateLuxReading(lux: Float) {
        currentLux = lux
        luxSum += lux
        luxSamplesCount++
        avgLux = (luxSum / luxSamplesCount).toFloat()
        if (lux > maxLux) maxLux = lux
        onSensorUpdate(currentLux, avgLux, disturbanceCount, isFaceDown)
    }

    fun simulateFlip() {
        if (isAlarmActive) {
            isFaceDown = true
            onFlipToSnooze()
        }
    }
}
