package com.dev_musashi.randomchat.presentation.chattingRoom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.usecases.ChangeConnectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChattingRoomViewModel @Inject constructor(
    private val changeConnectionStatus: ChangeConnectionStatus
) : ViewModel() {


    fun backButtonClick(popUp: ()->Unit){
        viewModelScope.launch {
            changeConnectionStatus("NotReady")
            popUp()
        }
    }
}