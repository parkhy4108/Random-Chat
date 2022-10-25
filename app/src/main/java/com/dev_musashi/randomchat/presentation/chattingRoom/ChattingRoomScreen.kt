package com.dev_musashi.randomchat.presentation.chattingRoom

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev_musashi.randomchat.util.BackHandler

@Composable
fun ChattingRoomScreen(
    roomID: String,
    popUp: ()->Unit,
    chattingRoomViewModel: ChattingRoomViewModel = hiltViewModel()
){
    Log.d("TAG", roomID)


    BackHandler(enabled = true) {
        chattingRoomViewModel.backButtonClick(popUp)
    }

}