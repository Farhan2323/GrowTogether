package com.example.growtogether.uihome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.growtogether.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@Composable
fun messageScreen(friendName: String, onBack: () -> Unit) {

    val AppBarColor = Color(0xFF67A1FF)

    val messages = remember { mutableStateListOf<String>() }
    var messageText by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = true,
                onClick = { focusManager.clearFocus() },
                interactionSource = interactionSource,
                indication = null
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppBarColor)   // AppBar color
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onBack() }
                )
                Text(
                    text = "Chat with $friendName",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedFlower(direction = "left")
            AnimatedFlower(direction = "right")
        }



        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier
                            .padding(6.dp)
                            .wrapContentWidth()
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (messageText.isNotBlank()) {
                    messages.add(messageText)
                    messageText = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun AnimatedFlower(direction: String = "left") {
    var stretched by remember { mutableStateOf(false) }

    val scaleX by animateFloatAsState(
        targetValue = if (stretched) 1f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flowerStretchX"
    )

    val scaleY by animateFloatAsState(
        targetValue = if (stretched) 1f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flowerStretchY"
    )

    LaunchedEffect(Unit) {
        stretched = true
    }


    val flipX = if (direction == "right") -1f else 1f

    Image(
        painter = painterResource(id = R.drawable.plantnew),
        contentDescription = null,
        modifier = Modifier
            .padding(10.dp)
            .size(120.dp)
            .graphicsLayer(
                scaleX = 1 / scaleX * flipX,   // flip for right flower
                scaleY = scaleY
            )
    )
}
