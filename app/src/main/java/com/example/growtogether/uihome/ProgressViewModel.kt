package com.example.growtogether.uihome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

/**
 * Simple state-holder (we call it ViewModel, but it’s just a Kotlin class).
 * - Tracks, for the current month, which tasks were completed on which day.
 * - Key = day of month (1..31), Value = set of completed task titles.
 */
class ProgressViewModel {

    var dailyHistory by mutableStateOf<Map<Int, Set<String>>>(emptyMap())
        private set

    fun updateTaskStatus(taskTitle: String, isDone: Boolean) {
        val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val currentForDay = dailyHistory[todayDay]?.toMutableSet() ?: mutableSetOf()
        if (isDone) {
            currentForDay.add(taskTitle)
        } else {
            currentForDay.remove(taskTitle)
        }

        dailyHistory = dailyHistory.toMutableMap().apply {
            put(todayDay, currentForDay.toSet())
        }
    }

    fun completedCountToday(): Int {
        val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        return dailyHistory[todayDay]?.size ?: 0
    }
}