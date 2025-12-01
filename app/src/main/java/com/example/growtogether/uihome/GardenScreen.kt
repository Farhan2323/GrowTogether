package com.example.growtogether.uihome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.UUID
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

data class Friend(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val plantLevel: Int = 0
)

@Composable
fun GardenScreen(onFriendClick: (String) -> Unit) {
    var friends by remember {
        mutableStateOf(
            listOf(
                Friend(name = "Farhan", plantLevel = 2),
                Friend(name = "Sruthi", plantLevel = 3),
                Friend(name = "Christopher", plantLevel = 1)
            )
        )
    }

    var newFriendName by remember { mutableStateOf("") }

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFEECDA3), Color(0xFFEF629F))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Garden",
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Cursive,
            fontSize = 40.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "See your friends' plant progress 🌿",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    friends = friends.map { it.copy(plantLevel = it.plantLevel + 1) }
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Message Friends", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newFriendName,
                onValueChange = { newFriendName = it },
                placeholder = { Text("Add a friend") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFEF629F),

                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(gradientBrush, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (newFriendName.isNotBlank()) {
                            friends = friends + Friend(name = newFriendName.trim(), plantLevel = 0)
                            newFriendName = ""
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Add", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Friends' Plants",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(friends, key = { it.id }) { friend ->
//                FriendRow(friend)
                FriendRow(friend, onClick = {
                    onFriendClick(friend.name)
                })
            }
        }
    }
}

//@Composable
//fun FriendRow(friend: Friend) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(horizontal = 12.dp, vertical = 8.dp)
//        ) {
//            Text(friend.name, style = MaterialTheme.typography.bodyLarge)
//            Text("Plant level: ${friend.plantLevel}", style = MaterialTheme.typography.bodyMedium)
//        }
//    }
//}

@Composable
fun FriendRow(friend: Friend, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEECDA3).copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(friend.name, style = MaterialTheme.typography.bodyLarge)
            Text("Plant level: ${friend.plantLevel}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
