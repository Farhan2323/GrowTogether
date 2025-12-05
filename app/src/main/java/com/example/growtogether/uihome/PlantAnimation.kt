package com.example.growtogether.uihome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.growtogether.R

@Composable
fun PlantStateAnimation(
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    // Map progress → "stage" of the plant
    // 0 tasks  -> baby sad
    // 1-2      -> baby happy
    // 3        -> mid happy
    // 4+       -> adult (overworked / stressed)
    val stage = when (completedCount) {
        0 -> 0
        1 -> 1
        2 -> 2
        3 -> 3
        else -> 4
    }


    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500),
        label = "plantScale"
    )


    val infinite = rememberInfiniteTransition(label = "plantSwayTransition")
    val sway by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plantSway"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = stage,
            label = "plantStage"
        ) { s ->
            val resId = when (s) {
                0 -> R.drawable.plant_baby_sad
                1 -> R.drawable.plant_baby
                2 -> R.drawable.plant_mid
                3 -> R.drawable.plantnew
                else -> R.drawable.plant_adult_sad
            }

            Image(
                painter = painterResource(resId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = sway
                )
            )
        }
    }
}

