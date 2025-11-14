package com.example.growtogether.uihome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressScreen() {
    val totalTasksCompleted = 59
    val dayStreak = 20
    val friendStreak = 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text("• Tasks completed: $totalTasksCompleted")
                Text("• 🌿 ${dayStreak}-day streak!")
                Text("• 💬 ${friendStreak}-day messaging streak with a friend")
            }
        }

        Spacer(Modifier.height(16.dp))

        CalendarMonth(
            monthLabel = "September",
            daysInMonth = 30,
            completedDays = setOf(1, 2, 3, 4, 5, 8, 9, 10, 12, 13, 15, 18, 19, 20, 22, 23, 24, 25, 26)
        )

        Spacer(Modifier.height(16.dp))

        CalendarMonth(
            monthLabel = "October",
            daysInMonth = 31,
            completedDays = setOf(1, 2, 3, 4, 10, 11, 12, 19, 20, 27, 28, 29, 30, 31)
        )
    }
}

@Composable
private fun CalendarMonth(
    monthLabel: String,
    daysInMonth: Int,
    completedDays: Set<Int>
) {
    Text(
        text = monthLabel,
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(Modifier.height(4.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    Spacer(Modifier.height(4.dp))

    var day = 1
    Column {
        while (day <= daysInMonth) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) {
                    if (day <= daysInMonth) {
                        DayCell(
                            day = day,
                            isCompleted = completedDays.contains(day)
                        )
                        day++
                    } else {
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DayCell(day: Int, isCompleted: Boolean) {
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

    Box(
        modifier = Modifier
            .size(32.dp)
            .then(
                if (isCompleted) Modifier.background(highlightColor)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
