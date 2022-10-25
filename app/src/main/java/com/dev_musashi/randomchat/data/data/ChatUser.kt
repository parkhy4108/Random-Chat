package com.dev_musashi.randomchat.data.data

data class ChatUser(
    val users: Map<String, Boolean> = mutableMapOf(),
    val comments: Map<String, String>? = null
)
