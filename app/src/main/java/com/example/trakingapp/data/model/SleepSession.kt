package com.example.trakingapp.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SleepSession(
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val targetWakeTime: String,
    val avgLux: Float,
    val maxLux: Float,
    val disturbanceCount: Int,
    val environmentRating: String,
    val notes: String = ""
) {
    val durationMinutes: Long
        get() = ((endTime - startTime) / (1000 * 60)).coerceAtLeast(1)

    val formattedDuration: String
        get() {
            val hours = durationMinutes / 60
            val minutes = durationMinutes % 60
            return if (hours > 0) "${hours}j ${minutes}m" else "${minutes} menit"
        }

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
            return sdf.format(Date(startTime))
        }

    val formattedStartTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(startTime))
        }

    val formattedEndTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(endTime))
        }
}

enum class EnvironmentRating(val title: String, val description: String) {
    OPTIMAL("Optimal", "Kamar gelap gulita & sangat tenang. Kondisi tidur ideal."),
    GOOD("Baik", "Kondisi kamar mendukung tidur nyenyak dengan sedikit gangguan."),
    FAIR("Cukup", "Terdapat sedikit cahaya atau pergerakan saat tidur."),
    NEEDS_IMPROVEMENT("Perlu Perbaikan", "Kamar terlalu terang atau terlalu banyak gangguan gerak.");

    companion object {
        fun calculate(avgLux: Float, disturbanceCount: Int): EnvironmentRating {
            return when {
                avgLux < 3.0f && disturbanceCount <= 2 -> OPTIMAL
                avgLux < 15.0f && disturbanceCount <= 6 -> GOOD
                avgLux < 40.0f && disturbanceCount <= 12 -> FAIR
                else -> NEEDS_IMPROVEMENT
            }
        }
    }
}
