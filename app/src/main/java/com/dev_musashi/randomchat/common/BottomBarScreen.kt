package com.dev_musashi.randomchat.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "Home",
        icon = Icons.Default.Home
    )
    object ChattingList : BottomBarScreen(
        route = "ChattingList",
        icon = Icons.Default.Search
    )
    object Profile : BottomBarScreen(
        route = "Profile",
        icon = Icons.Default.Person
    )
}
