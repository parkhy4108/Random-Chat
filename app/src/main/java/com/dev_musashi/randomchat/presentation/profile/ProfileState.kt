package com.dev_musashi.randomchat.presentation.profile

data class ProfileState(
    val nickName : String? = null,
    val userNickName: String = "",
    val userImage: String? = null,
    val isLoading: Boolean = false
)