package com.bitflip.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class HistoryItem(
    val tool: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

object HistoryManager {
    private const val PREFS_NAME = "bitflip_history_prefs"
    private const val KEY_HISTORY = "history_list"
    private const val MAX_ITEMS = 100 // Keep only last 100 items

    fun addHistory(context: Context, item: HistoryItem) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentHistory = getHistory(context).toMutableList()
        currentHistory.add(0, item) // Add to top

        if (currentHistory.size > MAX_ITEMS) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveHistory(prefs, currentHistory)
    }

    fun getHistory(context: Context): List<HistoryItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        val list = mutableListOf<HistoryItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    HistoryItem(
                        tool = obj.getString("tool"),
                        description = obj.getString("description"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(prefs: SharedPreferences, history: List<HistoryItem>) {
        val array = JSONArray()
        history.forEach { item ->
            val obj = JSONObject().apply {
                put("tool", item.tool)
                put("description", item.description)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }
}
