package com.dev_musashi.randomchat.presentation.home

data class HomeState(
    val isConnecting: Boolean = false,
    val connect: String = "no connection",
)
