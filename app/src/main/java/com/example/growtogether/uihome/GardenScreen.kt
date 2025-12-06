package com.example.growtogether.uihome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontFamily // Added this
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Added this
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.growtogether.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.graphicsLayer
import java.util.UUID


data class Friend(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val plantLevel: Int = 0
)

@Composable
fun GardenScreen(onFriendClick: (Friend) -> Unit) {

    var friends by remember {
        mutableStateOf(
            listOf(
                Friend(name = "Farhan", plantLevel = 3),
                Friend(name = "Sruthi", plantLevel = 3),
                Friend(name = "Chris", plantLevel = 3),
                Friend(name = "Sulaeman", plantLevel = 3)
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
//            .padding(16.dp)
    ) {

        val gridState = rememberLazyGridState()

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F4F6))) {

            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        // Move background WITH the scroll
                        translationY = -gridState.firstVisibleItemScrollOffset.toFloat()
                    },
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "My Garden",
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Cursive,
                            fontSize = 40.sp
                        )
//                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(friends, key = { it.id }) { friend ->
                    FriendCard(friend, onClick = {
                        onFriendClick(friend)
                    })
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
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
                                        friends = friends + Friend(
                                            name = newFriendName.trim(),
                                            plantLevel = 1
                                        )
                                        newFriendName = ""
                                    }
                                }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Add", color = Color.White)
                        }
                    }
                }
            }


        }

    }
}

@Composable
fun FriendCard(friend: Friend, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedFlower(growthLevel = friend.plantLevel, size = 1.5f)
            Text(friend.name)
            Text("Level: ${friend.plantLevel}")
        }
    }
}


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
            AnimatedFlower()
            Text("Plant level: ${friend.plantLevel}", style = MaterialTheme.typography.bodyMedium)
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


