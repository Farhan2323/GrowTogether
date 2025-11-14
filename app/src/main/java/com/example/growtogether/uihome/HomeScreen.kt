package com.example.growtogether.uihome

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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

        Spacer(Modifier.height(24.dp))

        // Plant Visualization
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            SimplePlant(completedCount = completedCount)
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
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
private fun SimplePlant(completedCount: Int) {
    @Suppress("UNUSED_PARAMETER")
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Draw pot
            val potTop = canvasHeight * 0.6f
            val potBottom = canvasHeight
            val potWidth = canvasWidth * 0.4f
            val potLeft = (canvasWidth - potWidth) / 2
            val potRight = potLeft + potWidth

            val potPath = Path().apply {
                moveTo(potLeft + 10, potTop)
                lineTo(potLeft, potBottom)
                lineTo(potRight, potBottom)
                lineTo(potRight - 10, potTop)
                close()
            }
            drawPath(
                path = potPath,
                color = Color(0xFF8B4513)
            )

            // Draw soil
            drawRect(
                color = Color(0xFF654321),
                topLeft = Offset(potLeft + 10, potTop),
                size = androidx.compose.ui.geometry.Size(
                    width = potWidth - 20,
                    height = 15f
                )
            )

            // Draw stem
            val stemWidth = 12f
            val stemHeight = 80f
            val stemCenterX = canvasWidth / 2
            drawRect(
                color = Color(0xFF228B22),
                topLeft = Offset(stemCenterX - stemWidth / 2, potTop - stemHeight),
                size = androidx.compose.ui.geometry.Size(stemWidth, stemHeight)
            )

            // Draw leaves
            val leafY = potTop - stemHeight / 2

            // Left leaf
            val leftLeafPath = Path().apply {
                moveTo(stemCenterX, leafY)
                cubicTo(
                    stemCenterX - 45, leafY - 15,
                    stemCenterX - 55, leafY,
                    stemCenterX - 50, leafY + 20
                )
                lineTo(stemCenterX, leafY)
            }
            drawPath(leftLeafPath, color = Color(0xFF32CD32))

            // Right leaf
            val rightLeafPath = Path().apply {
                moveTo(stemCenterX, leafY)
                cubicTo(
                    stemCenterX + 45, leafY - 15,
                    stemCenterX + 55, leafY,
                    stemCenterX + 50, leafY + 20
                )
                lineTo(stemCenterX, leafY)
            }
            drawPath(rightLeafPath, color = Color(0xFF32CD32))

            // Draw flower at the top
            val flowerCenterX = stemCenterX
            val flowerCenterY = potTop - stemHeight
            val petalRadius = 16f
            val centerRadius = 8f

            // Draw 5 petals in a circle
            for (i in 0 until 5) {
                val angle = (i * 72f - 90f) * Math.PI / 180f
                val petalX = flowerCenterX + (centerRadius * 1.5f * Math.cos(angle)).toFloat()
                val petalY = flowerCenterY + (centerRadius * 1.5f * Math.sin(angle)).toFloat()

                drawCircle(
                    color = Color(0xFFFF69B4), // Pink petals
                    radius = petalRadius,
                    center = Offset(petalX, petalY)
                )
            }

            // Draw flower center
            drawCircle(
                color = Color(0xFFFFD700), // Gold center
                radius = centerRadius,
                center = Offset(flowerCenterX, flowerCenterY)
            )
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
            .padding(vertical = 4.dp),
        colors = if (task.done) CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.2f)) else CardDefaults.cardColors()
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