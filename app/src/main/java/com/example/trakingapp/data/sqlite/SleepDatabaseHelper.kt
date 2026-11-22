package com.example.trakingapp.data.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.trakingapp.data.model.SleepSession

class SleepDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "nightstand_sleep.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "sleep_sessions"
        const val COLUMN_ID = "_id"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_END_TIME = "end_time"
        const val COLUMN_TARGET_WAKE_TIME = "target_wake_time"
        const val COLUMN_AVG_LUX = "avg_lux"
        const val COLUMN_MAX_LUX = "max_lux"
        const val COLUMN_DISTURBANCE_COUNT = "disturbance_count"
        const val COLUMN_ENVIRONMENT_RATING = "environment_rating"
        const val COLUMN_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_START_TIME INTEGER NOT NULL,
                $COLUMN_END_TIME INTEGER NOT NULL,
                $COLUMN_TARGET_WAKE_TIME TEXT NOT NULL,
                $COLUMN_AVG_LUX REAL NOT NULL,
                $COLUMN_MAX_LUX REAL NOT NULL,
                $COLUMN_DISTURBANCE_COUNT INTEGER NOT NULL,
                $COLUMN_ENVIRONMENT_RATING TEXT NOT NULL,
                $COLUMN_NOTES TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertSession(session: SleepSession): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_START_TIME, session.startTime)
            put(COLUMN_END_TIME, session.endTime)
            put(COLUMN_TARGET_WAKE_TIME, session.targetWakeTime)
            put(COLUMN_AVG_LUX, session.avgLux)
            put(COLUMN_MAX_LUX, session.maxLux)
            put(COLUMN_DISTURBANCE_COUNT, session.disturbanceCount)
            put(COLUMN_ENVIRONMENT_RATING, session.environmentRating)
            put(COLUMN_NOTES, session.notes)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllSessions(): List<SleepSession> {
        val sessionList = mutableListOf<SleepSession>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_START_TIME DESC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val session = cursorToSession(it)
                    sessionList.add(session)
                } while (it.moveToNext())
            }
        }
        return sessionList
    }

    fun getSessionById(id: Long): SleepSession? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToSession(it)
            }
        }
        return null
    }

    fun deleteSession(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun clearAllSessions(): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }

    private fun cursorToSession(cursor: Cursor): SleepSession {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val startTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME))
        val endTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
        val targetWake = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARGET_WAKE_TIME))
        val avgLux = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_AVG_LUX))
        val maxLux = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_MAX_LUX))
        val disturbance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISTURBANCE_COUNT))
        val rating = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENVIRONMENT_RATING))
        val notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)) ?: ""

        return SleepSession(
            id = id,
            startTime = startTime,
            endTime = endTime,
            targetWakeTime = targetWake,
            avgLux = avgLux,
            maxLux = maxLux,
            disturbanceCount = disturbance,
            environmentRating = rating,
            notes = notes
        )
    }
}
