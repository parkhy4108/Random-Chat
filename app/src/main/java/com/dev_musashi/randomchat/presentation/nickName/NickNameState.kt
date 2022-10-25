package com.dev_musashi.randomchat.presentation.nickName

data class NickNameState(
    val userNickName: String = "",
    val userImage : String? = null,
    val isLoading: Boolean = false
)
