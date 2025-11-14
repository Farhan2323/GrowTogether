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
import androidx.compose.ui.graphics.Color
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

@Composable
fun messageScreen(friendName: String) {
    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
//        Image(
//            painter = painterResource(id = R.drawable.flower2),
//            contentDescription = "Plant Image",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp) // adjust as needed
//        )
        AnimatedFlower()
        // Header
        Text(
            text = "Chat with $friendName",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        // Messages placeholder
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Messages will go here…")
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { messageText = "" }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun AnimatedFlower() {
    var stretched by remember { mutableStateOf(false) }

    // Animated scale value (1.0 → 1.2 → 1.0 → repeat)
    val scale by animateFloatAsState(
        targetValue = if (stretched) 1f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flowerStretch"
    )

    // Start animation automatically
    LaunchedEffect(Unit) { stretched = true }

    Image(
        painter = painterResource(id = R.drawable.flower2),
        contentDescription = null,
        modifier = Modifier
            .size(140.dp)
            .graphicsLayer(
                scaleY = scale,   // vertical stretch
                scaleX = 1f       // no horizontal scaling
            )
    )
}
