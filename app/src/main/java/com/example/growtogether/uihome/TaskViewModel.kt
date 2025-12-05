package com.example.growtogether.uihome

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class TaskViewModel : ViewModel() {

    var tasks by mutableStateOf(
        listOf(
            Task(title = "Message a friend"),
            Task(title = "Take a short walk"),
            Task(title = "Drink water")
        )
    )
        private set

    fun updateTask(id: String, done: Boolean) {
        tasks = tasks.map {
            if (it.id == id) it.copy(done = done) else it
        }
    }

    fun deleteTask(id: String) {
        tasks = tasks.filterNot { it.id == id }
    }
}
