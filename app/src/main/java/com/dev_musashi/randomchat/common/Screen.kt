package com.dev_musashi.randomchat.common

const val ARG_KEY = "id"
sealed class Screen(val route : String) {
    object Splash : Screen(route = "SPLASH")
    object Login: Screen(route = "LOGIN")
    object SignUp : Screen(route = "SIGNUP")
    object NickName: Screen(route = "NICKNAME")
    object ChattingRoom : Screen(route = "CHATTINGROOM/{$ARG_KEY}")
        fun passRoomId(id: String) : String {
            return this.route.replace(
                oldValue = "{$ARG_KEY}",
                newValue = id
            )
        }
}