package com.example.growtogether.uihome
import com.example.growtogether.uihome.ProgressViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

enum class BottomTab {
    HOME,
    GARDEN,
    PROGRESS
}

@Composable
fun GrowTogetherApp() {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var activeFriendChat by remember { mutableStateOf<String?>(null) }

    var conversations by remember {
        mutableStateOf<Map<String, List<String>>>(emptyMap())
    }
    val progressViewModel = remember { ProgressViewModel() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.HOME,
                    onClick = {
                        activeFriendChat = null
                        selectedTab = BottomTab.HOME
                    },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.GARDEN,
                    onClick = {
                        activeFriendChat = null
                        selectedTab = BottomTab.GARDEN
                    },
                    icon = { Text("🌿") },
                    label = { Text("Garden") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.PROGRESS,
                    onClick = {
                        activeFriendChat = null
                        selectedTab = BottomTab.PROGRESS
                    },
                    icon = { Text("📈") },
                    label = { Text("Progress") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val friendName = activeFriendChat
            if (friendName != null) {
                val friendMessages = conversations[friendName] ?: emptyList()
                messageScreen(
                    friendName = friendName,
                    messages = friendMessages,
                    onSendMessage = { text: String ->
                        val current = conversations[friendName] ?: emptyList()
                        conversations = conversations.toMutableMap().apply {
                            put(friendName, current + text)
                        }
                    },
                    onBack = { activeFriendChat = null}
                )
                return@Box
            }
            when (selectedTab) {
                BottomTab.HOME -> HomeScreen(progressViewModel = progressViewModel)
                BottomTab.GARDEN -> GardenScreen(
                    onFriendClick = { clickedFriendName ->
                        activeFriendChat = clickedFriendName
                    }
                )
                BottomTab.PROGRESS -> ProgressScreen(progressViewModel = progressViewModel)
            }
        }
    }
}

