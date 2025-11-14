package com.example.growtogether.uihome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val done: Boolean = false
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(title = "Message a friend"),
                Task(title = "Take a short walk"),
                Task(title = "Drink water")
            )
        )
    }

    val completedCount = tasks.count { it.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Grow Together",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your plant has grown from $completedCount completed task(s) 🌱",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Today's Tasks (swipe left to delete)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))


        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { value: DismissValue ->
                        if (value == DismissValue.DismissedToStart) {
                            tasks = tasks.filterNot { it.id == task.id }
                            true
                        } else {
                            false
                        }
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
                            onCheckedChange = { checked ->
                                tasks = tasks.map {
                                    if (it.id == task.id) it.copy(done = checked)
                                    else it
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun TaskRow(
    task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = onCheckedChange
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
