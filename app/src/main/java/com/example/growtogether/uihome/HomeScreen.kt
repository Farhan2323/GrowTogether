package com.example.growtogether.uihome

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID
import androidx.compose.material3.CheckboxDefaults

//keep these changes
//import androidx.compose.ui.graphics.Color

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val done: Boolean = false
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(taskViewModel: TaskViewModel, progressViewModel: ProgressViewModel) {


//    var tasks by remember {
//        mutableStateOf(
//            listOf(
//                Task(title = "Message a friend"),
//                Task(title = "Take a short walk"),
//                Task(title = "Drink water")
//            )
//        )
//    }
    val tasks = taskViewModel.tasks

    val completedCount = progressViewModel.completedCountToday()
    var completedTaskTitle by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Grow Together",
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Cursive,
            fontSize = 40.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your plant has grown from $completedCount completed task(s) 🌱",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))

// 🌿 Add this animation here!
        PlantStateAnimation(completedCount = completedCount)

        Spacer(Modifier.height(16.dp))
//        HorizontalDivider()
        Spacer(Modifier.height(8.dp))



        Spacer(Modifier.height(24.dp))

        // Plant Visualization
        /*
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
//            SimplePlant(completedCount = completedCount)
            AnimatedFlower()
        }
        */
        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Today's Tasks (swipe left to delete)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))


        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = tasks.sortedBy { it.done },
                key = { it.id }
            ) { task ->
                val dismissState = rememberDismissState(
//                    confirmStateChange = { value: DismissValue ->
//                        if (value == DismissValue.DismissedToStart) {
//                            tasks = tasks.filterNot { it.id == task.id }
//                            true
//                        } else {
//                            false
//                        }
//                    }
                    confirmStateChange = { value ->
                        if (value == DismissValue.DismissedToStart) {
                            taskViewModel.deleteTask(task.id)
                            true
                        } else false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Delete",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    dismissContent = {
                        TaskRow(
                            task = task,
//                            onCheckedChange = { checked ->
//                                tasks = tasks.map {
//                                    if (it.id == task.id) it.copy(done = checked)
//                                    else it
//                                }
//
//                                // Save this change into shared progress
//                                progressViewModel.updateTaskStatus(task.title, checked)
//                            }
                            onCheckedChange = { checked ->
                                taskViewModel.updateTask(task.id, checked)
                                progressViewModel.updateTaskStatus(task.title, checked)
                                if (checked) {
                                    completedTaskTitle = task.title
                                }
                            }
                        )

                    }
                )
            }
        }

        completedTaskTitle?.let { taskTitle ->
            TaskCompletionDialog(
                taskTitle = taskTitle,
                onDismiss = { completedTaskTitle = null }
            )
        }
    }
}

@Composable
private fun TaskCompletionDialog(
    taskTitle: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Task Completed! ✨") },
        text = { Text("You're doing great! You've successfully completed:\n\n\"$taskTitle\"") },
        confirmButton = {
            Box(
                modifier = Modifier
                    .background(Color(0xFFEF629F), shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Continue", color = Color.White)
            }
        },
        containerColor = Color(0xFFFFF7F8),
    )
}

@Composable
private fun TaskRow(
    task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    val completedCardColor = Color(0xFFD5E8D4) // A soft, earthy green
    val completedTextColor = Color(0xFF135413) // A dark, forest green
    val defaultCardColor = Color(0xFFF5F5DC) // A gentle beige/off-white


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        // Use our new colors based on the task's 'done' state
        colors = if (task.done) {
            CardDefaults.cardColors(containerColor = completedCardColor)
        } else {
            CardDefaults.cardColors(containerColor = defaultCardColor)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = completedTextColor, // Use the dark green for the check mark
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant, // Keep default for unchecked
                    checkmarkColor = completedCardColor // Use the light green for the check mark itself
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = task.title,
                // Use the dark green for completed tasks for better contrast
                color = if (task.done) completedTextColor else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
