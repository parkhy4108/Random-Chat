package com.dev_musashi.randomchat.domain.model

import android.net.Uri

data class User(
    val userName: String,
    val userImage: Uri?,
    val uid: String
)
