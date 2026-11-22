package com.example.trakingapp.data.repository

import android.content.Context
import com.example.trakingapp.data.model.SleepSession
import com.example.trakingapp.data.sqlite.SleepDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SleepSummaryStats(
    val totalNights: Int = 0,
    val avgDurationMinutes: Long = 0,
    val avgDisturbances: Float = 0f,
    val avgRoomBrightness: Float = 0f,
    val optimalNightsCount: Int = 0
) {
    val formattedAvgDuration: String
        get() {
            val hours = avgDurationMinutes / 60
            val minutes = avgDurationMinutes % 60
            return if (hours > 0) "${hours}j ${minutes}m" else "${minutes}m"
        }
}

class SleepRepository(context: Context) {
    private val dbHelper = SleepDatabaseHelper(context.applicationContext)

    suspend fun saveSession(session: SleepSession): Long = withContext(Dispatchers.IO) {
        dbHelper.insertSession(session)
    }

    suspend fun getAllSessions(): List<SleepSession> = withContext(Dispatchers.IO) {
        dbHelper.getAllSessions()
    }

    suspend fun deleteSession(id: Long): Boolean = withContext(Dispatchers.IO) {
        dbHelper.deleteSession(id) > 0
    }

    suspend fun clearHistory(): Boolean = withContext(Dispatchers.IO) {
        dbHelper.clearAllSessions() >= 0
    }

    suspend fun getSummaryStats(): SleepSummaryStats = withContext(Dispatchers.IO) {
        val sessions = dbHelper.getAllSessions()
        if (sessions.isEmpty()) {
            return@withContext SleepSummaryStats()
        }

        val totalNights = sessions.size
        val avgDuration = sessions.map { it.durationMinutes }.average().toLong()
        val avgDisturbances = sessions.map { it.disturbanceCount }.average().toFloat()
        val avgLux = sessions.map { it.avgLux }.average().toFloat()
        val optimalCount = sessions.count { it.environmentRating.equals("Optimal", ignoreCase = true) }

        SleepSummaryStats(
            totalNights = totalNights,
            avgDurationMinutes = avgDuration,
            avgDisturbances = avgDisturbances,
            avgRoomBrightness = avgLux,
            optimalNightsCount = optimalCount
        )
    }
}
