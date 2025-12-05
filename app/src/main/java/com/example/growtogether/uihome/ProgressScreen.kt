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

    val initialCalendar = Calendar.getInstance()
    var displayedYear by remember { mutableStateOf(initialCalendar.get(Calendar.YEAR)) }
    var displayedMonthIndex by remember { mutableStateOf(initialCalendar.get(Calendar.MONTH)) }

    val completeHistory = remember {
        val currentYear = initialCalendar.get(Calendar.YEAR)
        val currentMonth = initialCalendar.get(Calendar.MONTH)

        val prevMonthCalendar = (initialCalendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
        val prevYear = prevMonthCalendar.get(Calendar.YEAR)
        val prevMonth = prevMonthCalendar.get(Calendar.MONTH)

        mapOf(
            (prevYear to prevMonth) to generateDummyDataForMonth(prevYear, prevMonth),
            (currentYear to currentMonth) to progressViewModel.dailyHistory
        )
    }

    val dailyHistory = completeHistory[displayedYear to displayedMonthIndex] ?: emptyMap()

    val displayedCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, displayedYear)
        set(Calendar.MONTH, displayedMonthIndex)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = displayedCalendar.get(Calendar.DAY_OF_WEEK)
    val emptyCellsAtStart = firstDayOfWeek - 1

    val todayDay = if (displayedYear == initialCalendar.get(Calendar.YEAR) && displayedMonthIndex == initialCalendar.get(Calendar.MONTH)) {
        initialCalendar.get(Calendar.DAY_OF_MONTH)
    } else -1 // Not today if not the current month

    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

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
                    text = "Let's see how you're doing!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (completeHistory.values.all { it.isEmpty() }) {
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
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        var newMonth = displayedMonthIndex - 1
                        var newYear = displayedYear
                        if (newMonth < 0) {
                            newMonth = 11
                            newYear--
                        }
                        displayedMonthIndex = newMonth
                        displayedYear = newYear
                    }) { Text("<") }

                    Text(
                        text = "${monthNames[displayedMonthIndex]} $displayedYear",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(onClick = {
                        var newMonth = displayedMonthIndex + 1
                        var newYear = displayedYear
                        if (newMonth > 11) {
                            newMonth = 0
                            newYear++
                        }
                        if (newYear < initialCalendar.get(Calendar.YEAR) ||
                            (newYear == initialCalendar.get(Calendar.YEAR) && newMonth <= initialCalendar.get(Calendar.MONTH))) {
                            displayedMonthIndex = newMonth
                            displayedYear = newYear
                        }
                    }) { Text(">") }
                }

                Spacer(Modifier.height(12.dp))

                val overviewBrush = Brush.horizontalGradient(
                    listOf(Color(0xFFEF629F), Color(0xFFEECDA3))
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
                monthLabel = monthNames[displayedMonthIndex],
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

    val borderModifier = if (isToday) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .then(borderModifier)
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
    for (day in 1..daysInMonth) {
        val active = dailyHistory[day]?.isNotEmpty() == true
        if (active) {
            current++
        } else {
            best = maxOf(best, current)
            current = 0
        }
    }
    return maxOf(best, current)
}

private fun generateDummyDataForMonth(year: Int, month: Int): Map<Int, Set<String>> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
    }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dummyHistory = mutableMapOf<Int, Set<String>>()
    val tasks = listOf("Message a friend", "Take a short walk", "Drink water", "Read a chapter", "Meditate")

    for (day in 1..daysInMonth) {
        if (Math.random() > 0.3) { // 70% chance to have tasks
            val taskCount = (1..3).random()
            dummyHistory[day] = tasks.shuffled().take(taskCount).toSet()
        }
    }
    return dummyHistory
}
