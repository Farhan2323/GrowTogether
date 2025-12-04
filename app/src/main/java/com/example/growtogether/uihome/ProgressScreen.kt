package com.example.growtogether.uihome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun ProgressScreen(progressViewModel: ProgressViewModel) {

    val dailyHistory = progressViewModel.dailyHistory
    val calendar = Calendar.getInstance()
    val currentMonthIndex = calendar.get(Calendar.MONTH) // 0..11
    val currentYear = calendar.get(Calendar.YEAR)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val totalTasks = dailyHistory.values.sumOf { it.size }
    val activeDays = dailyHistory.keys.size
    val longestStreak = longestStreak(dailyHistory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Cursive,
            fontSize = 40.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Let's see how you're doing this month!",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${monthNames[currentMonthIndex]} $currentYear",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        // 🌈 Overview card with soft gradient
        val overviewBrush = Brush.horizontalGradient(
            listOf(Color(0xFFEECDA3), Color(0xFFEF629F))
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .background(overviewBrush)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "This month at a glance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("✅ Tasks completed: $totalTasks", color = Color.White)
                    Text("📅 Active days: $activeDays", color = Color.White)
                    Text("🔥 Longest streak: $longestStreak days", color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Colors: 🔴 low • 🟡 medium • 🟢 high • 🌱 today • 🔥 perfect day",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        CalendarMonth(
            daysInMonth = daysInMonth,
            todayDay = todayDay,
            dailyHistory = dailyHistory,
            onClickDay = { day -> selectedDay = day }
        )

        selectedDay?.let { day ->
            DayDetailDialog(
                day = day,
                tasks = dailyHistory[day] ?: emptySet(),
                monthLabel = monthNames[currentMonthIndex],
                onDismiss = { selectedDay = null }
            )
        }
    }
}

@Composable
private fun CalendarMonth(
    daysInMonth: Int,
    todayDay: Int,
    dailyHistory: Map<Int, Set<String>>,
    onClickDay: (Int) -> Unit
) {
    // Day-of-week header (simple Sunday-start calendar)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    var day = 1

    Column {
        while (day <= daysInMonth) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) {
                    if (day <= daysInMonth) {
                        // 🔑 capture the current value BEFORE incrementing
                        val thisDay = day
                        val completedCount = dailyHistory[thisDay]?.size ?: 0

                        DayCell(
                            day = thisDay,
                            completedTasks = completedCount,
                            isToday = (thisDay == todayDay),
                            totalTasksPerDay = 3, // you currently have 3 daily tasks
                            onClick = { onClickDay(thisDay) }
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
private fun DayCell(
    day: Int,
    completedTasks: Int,
    isToday: Boolean,
    totalTasksPerDay: Int,
    onClick: () -> Unit
) {
    // heatmap-ish colors
    val bgColor = when (completedTasks) {
        0 -> Color.Transparent
        1 -> Color(0xFFFFCDD2)   // light red
        2 -> Color(0xFFFFF9C4)   // light yellow
        else -> Color(0xFFC8E6C9) // light green
    }

    val isPerfect = completedTasks >= totalTasksPerDay

    val label = buildString {
        append(day)
        if (isToday) append(" 🌱")
        if (isPerfect) append(" 🔥")
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DayDetailDialog(
    day: Int,
    tasks: Set<String>,
    monthLabel: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        },
        title = {
            Text(text = "$monthLabel $day")
        },
        text = {
            if (tasks.isEmpty()) {
                Text(
                    "No tasks were completed on this day.\n" +
                            "One calm day doesn’t hurt your garden — getting back to it tomorrow keeps your plant thriving 🌱"
                )
            } else {
                Column {
                    Text(
                        text = "Tasks completed:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    tasks.forEach {
                        Text("• $it")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Nice work! Days like this are what grow your plant the fastest 🌿",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

/**
 * Simple longest-streak calculator:
 * streak = consecutive days (1..31) with at least one task.
 */
private fun longestStreak(dailyHistory: Map<Int, Set<String>>): Int {
    var current = 0
    var best = 0
    for (day in 1..31) {
        val active = dailyHistory[day]?.isNotEmpty() == true
        if (active) {
            current++
            if (current > best) best = current
        } else {
            current = 0
        }
    }
    return best
}

