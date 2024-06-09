package com.partitionsoft.trackingexpenseapplication.data.local

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val PREFS_NAME = "tracking_expense_prefs"
    private const val KEY_LAST_UPDATE_TIME = "last_update_time"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setLastUpdateTime(context: Context, time: Long) {
        getPreferences(context).edit().putLong(KEY_LAST_UPDATE_TIME, time).apply()
    }

    fun getLastUpdateTime(context: Context): Long {
        return getPreferences(context).getLong(KEY_LAST_UPDATE_TIME, 0L)
    }
}