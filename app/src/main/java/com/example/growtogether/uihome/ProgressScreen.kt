package com.example.growtogether.uihome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// --- Data models for simulated progress ---

enum class HabitTask(val displayName: String) {
    MESSAGE_FRIEND("Message a friend"),
    SHORT_WALK("Take a short walk"),
    DRINK_WATER("Drink water"),
    GO_GYM("Go to the gym"),
    CALL_MOM("Call mom")
}

data class DayProgress(
    val day: Int,
    val completedTasks: Set<HabitTask>
)

data class MonthProgress(
    val name: String,
    val daysInMonth: Int,
    val progressByDay: Map<Int, DayProgress>
)

/**
 * Extra stats for streaks + points.
 * points: score for the day
 * perfectStreak: length of consecutive days up to this day where ALL tasks were done
 */
data class DayStats(
    val points: Int,
    val perfectStreak: Int
)

// --- Main screen ---

@Composable
fun ProgressScreen() {
    // Simulated long-term data for a "healthy", consistent user
    val months = remember { sampleProgressData() }

    // Day-level stats (points + perfect streaks), computed across months in order
    val dayStatsByMonth = remember { calculateDayStatsByMonth(months) }

    // Aggregate statistics
    val totalTasksCompleted = months.sumOf { month ->
        month.progressByDay.values.sumOf { it.completedTasks.size }
    }

    val totalPoints = dayStatsByMonth.sumOf { monthMap ->
        monthMap.values.sumOf { it.points }
    }

    val dayStreak = calculateStreak(
        months = months,
        predicate = { day -> day?.completedTasks?.isNotEmpty() == true }
    )

    val friendStreak = calculateStreak(
        months = months,
        predicate = { day -> day?.completedTasks?.contains(HabitTask.MESSAGE_FRIEND) == true }
    )

    val longestPerfectStreak = dayStatsByMonth.maxOfOrNull { monthMap ->
        monthMap.values.maxOfOrNull { it.perfectStreak } ?: 0
    } ?: 0

    // Dialog state
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedProgress by remember { mutableStateOf<DayProgress?>(null) }
    var selectedStats by remember { mutableStateOf<DayStats?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }

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

        // Overview card with stats
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
                Text("• ✅ Tasks completed: $totalTasksCompleted")
                Text("• ⭐ Total points: $totalPoints")
                Text("• 🌿 Longest activity streak: $dayStreak days")
                Text("• 💬 Longest messaging streak: $friendStreak days")
                Text("• 🔥 Longest perfect streak: $longestPerfectStreak days")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Calendar colors: 🔴 low • 🟡 medium • 🟢 high points",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Legend: 🔥 = perfect habit day (all 5 tasks completed)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Render each month calendar
        months.forEachIndexed { index, month ->
            val statsForMonth = dayStatsByMonth[index]

            CalendarMonth(
                monthLabel = month.name,
                daysInMonth = month.daysInMonth,
                dailyProgress = month.progressByDay,
                dailyStats = statsForMonth
            ) { day, progress, stats ->
                selectedMonth = month.name
                selectedDay = day
                selectedProgress = progress
                selectedStats = stats
                isDialogOpen = true
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Day details dialog
    if (isDialogOpen && selectedMonth != null && selectedDay != null) {
        val progress = selectedProgress
        val stats = selectedStats

        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                Button(onClick = { isDialogOpen = false }) {
                    Text("Close")
                }
            },
            title = {
                Text(text = "${selectedMonth!!} ${selectedDay!!}")
            },
            text = {
                if (progress == null || progress.completedTasks.isEmpty()) {
                    Column {
                        Text(
                            text = "No tasks were completed on this day.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "You’ve been doing great overall — one rest day won’t break your growth. Getting back to all five habits keeps your streaks and points climbing. 🌱",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Column {
                        Text(
                            text = "Tasks completed:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))

                        HabitTask.values().forEach { habit ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = progress.completedTasks.contains(habit),
                                    onCheckedChange = null // read-only
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(text = habit.displayName)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        val completedCount = progress.completedTasks.size
                        val totalHabits = HabitTask.values().size
                        val completionText =
                            "You completed $completedCount of $totalHabits habits on this day."

                        Text(
                            text = completionText,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(Modifier.height(8.dp))

                        if (stats != null) {
                            Text(
                                text = "Points earned: ${stats.points}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (completedCount == totalHabits && stats.perfectStreak > 0) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Perfect streak status: this day is part of a ${stats.perfectStreak}-day run of completing all habits in a row. 🔥",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

// --- Calendar composables ---

@Composable
private fun CalendarMonth(
    monthLabel: String,
    daysInMonth: Int,
    dailyProgress: Map<Int, DayProgress>,
    dailyStats: Map<Int, DayStats>,
    onDaySelected: (Int, DayProgress?, DayStats?) -> Unit
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
                        val progressForDay = dailyProgress[day]
                        val statsForDay = dailyStats[day]
                        val points = statsForDay?.points ?: 0
                        val totalHabits = HabitTask.values().size
                        val isPerfect = progressForDay?.completedTasks?.size == totalHabits

                        DayCell(
                            day = day,
                            points = points,
                            isPerfect = isPerfect,
                            onClick = { onDaySelected(day, progressForDay, statsForDay) }
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
    points: Int,
    isPerfect: Boolean,
    onClick: () -> Unit
) {
    // With 5 tasks + streak bonus, full days quickly get high points:
    // 0 = no color, 1–2 = low (red), 3–5 = medium (yellow), 6+ = high (green)
    val backgroundColor = when {
        points <= 0 -> Color.Transparent
        points in 1..2 -> Color(0xFFFFCDD2) // light red
        points in 3..5 -> Color(0xFFFFF9C4) // light yellow
        else -> Color(0xFFC8E6C9)          // light green
    }

    val label = if (isPerfect) "$day🔥" else day.toString()

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// --- Simulated data, points & streak helpers ---

/**
 * Simulate a consistently healthy user:
 *  - Most days have all five habits completed
 *  - Only a few “lighter” days or rest days
 *  - Several long perfect streaks across months
 */
private fun sampleProgressData(): List<MonthProgress> {
    val all = setOf(
        HabitTask.MESSAGE_FRIEND,
        HabitTask.SHORT_WALK,
        HabitTask.DRINK_WATER,
        HabitTask.GO_GYM,
        HabitTask.CALL_MOM
    )

    val socialMovement = setOf(
        HabitTask.MESSAGE_FRIEND,
        HabitTask.SHORT_WALK,
        HabitTask.GO_GYM
    )

    val recoveryHydration = setOf(
        HabitTask.DRINK_WATER,
        HabitTask.GO_GYM,
        HabitTask.CALL_MOM
    )

    val lightConnection = setOf(
        HabitTask.MESSAGE_FRIEND,
        HabitTask.CALL_MOM
    )

    val septemberMap = mutableMapOf<Int, DayProgress>()
    // Sept 1–10: perfect streak of 10 days (all 5 habits)
    for (d in 1..10) {
        septemberMap[d] = DayProgress(d, all)
    }
    // Sept 11–12: still very good, 3 tasks
    septemberMap[11] = DayProgress(11, socialMovement)
    septemberMap[12] = DayProgress(12, recoveryHydration)
    // Sept 13–20: another perfect streak (8 days)
    for (d in 13..20) {
        septemberMap[d] = DayProgress(d, all)
    }
    // Sept 21: light day, 2 tasks
    septemberMap[21] = DayProgress(21, lightConnection)
    // Sept 22–27: perfect again
    for (d in 22..27) {
        septemberMap[d] = DayProgress(d, all)
    }
    // Sept 28–30: still healthy, 3 tasks per day
    septemberMap[28] = DayProgress(28, socialMovement)
    septemberMap[29] = DayProgress(29, recoveryHydration)
    septemberMap[30] = DayProgress(30, socialMovement)

    val september = MonthProgress(
        name = "September",
        daysInMonth = 30,
        progressByDay = septemberMap
    )

    val octoberMap = mutableMapOf<Int, DayProgress>()
    // Oct 1–7: perfect streak
    for (d in 1..7) {
        octoberMap[d] = DayProgress(d, all)
    }
    // Oct 8: lighter day
    octoberMap[8] = DayProgress(8, lightConnection)
    // Oct 9–15: another perfect streak
    for (d in 9..15) {
        octoberMap[d] = DayProgress(d, all)
    }
    // Oct 16: true rest day (no entry)
    // Oct 17–24: mostly all tasks, with a couple 3-task days
    octoberMap[17] = DayProgress(17, all)
    octoberMap[18] = DayProgress(18, recoveryHydration)
    octoberMap[19] = DayProgress(19, all)
    octoberMap[20] = DayProgress(20, all)
    octoberMap[21] = DayProgress(21, socialMovement)
    octoberMap[22] = DayProgress(22, all)
    octoberMap[23] = DayProgress(23, all)
    octoberMap[24] = DayProgress(24, all)
    // Oct 25–31: strong finish with mostly perfect days
    for (d in 25..31) {
        val tasks = if (d == 27 || d == 29) recoveryHydration else all
        octoberMap[d] = DayProgress(d, tasks)
    }

    val october = MonthProgress(
        name = "October",
        daysInMonth = 31,
        progressByDay = octoberMap
    )

    return listOf(september, october)
}

/**
 * Calculates day stats (points + perfect streak length) for each month/day.
 * Points:
 *  - 1 point per completed habit
 *  - If ALL habits are done, you get a streak bonus equal to the current
 *    length of the perfect streak (including this day).
 *      e.g. 1st perfect day: 5 + 1 = 6 points
 *           2nd perfect day in a row: 5 + 2 = 7 points, etc.
 */
private fun calculateDayStatsByMonth(
    months: List<MonthProgress>
): List<Map<Int, DayStats>> {
    val result = MutableList(months.size) { mutableMapOf<Int, DayStats>() }
    var currentPerfectStreak = 0

    months.forEachIndexed { monthIndex, month ->
        for (day in 1..month.daysInMonth) {
            val progress = month.progressByDay[day]
            if (progress != null) {
                val completedCount = progress.completedTasks.size
                val totalHabits = HabitTask.values().size
                val isPerfect = completedCount == totalHabits

                if (isPerfect) {
                    currentPerfectStreak++
                } else {
                    currentPerfectStreak = 0
                }

                val basePoints = completedCount
                val streakBonus = if (isPerfect) currentPerfectStreak else 0
                val points = basePoints + streakBonus

                result[monthIndex][day] = DayStats(
                    points = points,
                    perfectStreak = if (isPerfect) currentPerfectStreak else 0
                )
            } else {
                // No activity breaks the perfect streak
                currentPerfectStreak = 0
            }
        }
    }

    return result
}

/**
 * Calculates the longest streak of days where [predicate] is true.
 * Days with no entry break the streak.
 */
private fun calculateStreak(
    months: List<MonthProgress>,
    predicate: (DayProgress?) -> Boolean
): Int {
    var currentStreak = 0
    var bestStreak = 0

    months.forEach { month ->
        for (day in 1..month.daysInMonth) {
            val progress = month.progressByDay[day]
            if (predicate(progress)) {
                currentStreak++
                if (currentStreak > bestStreak) bestStreak = currentStreak
            } else {
                currentStreak = 0
            }
        }
    }

    return bestStreak
}

