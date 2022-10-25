package com.dev_musashi.randomchat.util

sealed class Screen(val route : String) {
    object Splash : Screen(route = "SPLASH")
    object Login: Screen(route = "LOGIN")
    object SignUp : Screen(route = "SIGNUP")
    object NickName: Screen(route = "NICKNAME")
    object ChattingRoom : Screen(route = "CHATTING")
}