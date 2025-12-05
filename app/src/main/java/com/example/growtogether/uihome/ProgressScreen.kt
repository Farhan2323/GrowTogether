package com.example.growtogether.uihome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun ProgressScreen(progressViewModel: ProgressViewModel) {

    val dailyHistory = progressViewModel.dailyHistory
    val calendar = Calendar.getInstance()
    val currentMonthIndex = calendar.get(Calendar.MONTH) // 0..11
    val currentYear = calendar.get(Calendar.YEAR)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

    val firstDayOfMonthCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonthIndex)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstDayOfWeek = firstDayOfMonthCalendar.get(Calendar.DAY_OF_WEEK)
    val emptyCellsAtStart = firstDayOfWeek - 1

    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // --- IMPROVEMENT: Correctly calculate streak and animate visibility ---
    val longestStreak = longestStreak(dailyHistory, daysInMonth)
    val totalTasks = dailyHistory.values.sumOf { it.size }
    val activeDays = dailyHistory.keys.size

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- IMPROVEMENT: Animate title visibility ---
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                    slideInVertically(initialOffsetY = { -40 })
        ) {
            Column {
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
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- IMPROVEMENT: Add an empty state for new users ---
        if (dailyHistory.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🌱", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Your journey begins!",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "Complete your first task on the Home screen to plant a seed in your calendar.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            // --- Regular content for users with history ---
            Column {
                Text(
                    text = "${monthNames[currentMonthIndex]} $currentYear",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                // Overview card
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
                            .fillMaxWidth()
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
                                text = "Colors:\n🔴 low      • 🟡 medium       • 🟢 high\n🌱 today  • 🔥 perfect day",
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
                    emptyCellsAtStart = emptyCellsAtStart,
                    onClickDay = { day -> selectedDay = day }
                )
            }
        }

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
    emptyCellsAtStart: Int,
    onClickDay: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    val totalCells = emptyCellsAtStart + daysInMonth
    (0 until totalCells).chunked(7).forEach { week ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (i in 0 until 7) {
                if (i < week.size) {
                    val dayIndex = week[i]
                    if (dayIndex >= emptyCellsAtStart) {
                        val day = dayIndex - emptyCellsAtStart + 1
                        val completedCount = dailyHistory[day]?.size ?: 0

                        DayCell(
                            day = day,
                            completedTasks = completedCount,
                            isToday = (day == todayDay),
                            totalTasksPerDay = 3,
                            onClick = { onClickDay(day) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
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
    val bgColor = when (completedTasks) {
        0 -> Color.Transparent
        1 -> Color(0xFFFFCDD2)   // light red
        2 -> Color(0xFFFFF9C4)   // light yellow
        else -> Color(0xFFC8E6C9) // light green
    }

    val isPerfect = completedTasks >= totalTasksPerDay
    val label = buildString {
        append(day)
        if (isToday && !isPerfect) append(" 🌱")
        if (isPerfect) append(" 🔥")
    }

    // --- IMPROVEMENT: Add a border for today's date ---
    val borderModifier = if (isToday) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .then(borderModifier) // Apply the border here
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
        title = { Text(text = "$monthLabel $day") },
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

private fun longestStreak(dailyHistory: Map<Int, Set<String>>, daysInMonth: Int): Int {
    var current = 0
    var best = 0
    // Loop only through the days of the current month
    for (day in 1..daysInMonth) {
        val active = dailyHistory[day]?.isNotEmpty() == true
        if (active) {
            current++
        } else {
            // Reset the streak if a day is missed, but save the best score first
            best = maxOf(best, current)
            current = 0
        }
    }
    // Final check in case the month ends on an active streak
    return maxOf(best, current)
}
