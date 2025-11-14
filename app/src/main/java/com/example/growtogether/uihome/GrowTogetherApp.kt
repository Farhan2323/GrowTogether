package com.example.growtogether.uihome


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
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.HOME,
//                    onClick = { selectedTab = BottomTab.HOME },
                    onClick = {
                        activeFriendChat = null
                        selectedTab = BottomTab.HOME
                    },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.GARDEN,
//                    onClick = { selectedTab = BottomTab.GARDEN },
                    onClick = {
                        activeFriendChat = null
                        selectedTab = BottomTab.GARDEN
                    },
                    icon = { Text("🌿") },
                    label = { Text("Garden") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.PROGRESS,

//                    onClick = { selectedTab = BottomTab.PROGRESS },
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
            if (activeFriendChat != null) {
                messageScreen(friendName = activeFriendChat!!)
                return@Box
            }

            when (selectedTab) {
                BottomTab.HOME -> HomeScreen()
//                BottomTab.GARDEN -> GardenScreen()
                BottomTab.GARDEN -> GardenScreen(
                    onFriendClick = { friendName: String ->
                        activeFriendChat = friendName
                    }
                )
                BottomTab.PROGRESS -> ProgressScreen()
            }
        }
    }
}
