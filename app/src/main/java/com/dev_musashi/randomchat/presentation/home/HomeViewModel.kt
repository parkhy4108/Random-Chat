package com.dev_musashi.randomchat.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.R.string as AppText
import com.dev_musashi.randomchat.domain.usecases.CancelConnect
import com.dev_musashi.randomchat.domain.usecases.ConnectUser
import com.dev_musashi.randomchat.domain.usecases.Online
import com.dev_musashi.randomchat.util.Screen
import com.dev_musashi.randomchat.util.SnackBarManager
import com.dev_musashi.randomchat.util.SnackBarMessage.Companion.toSnackBarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectUser: ConnectUser,
    private val cancelConnect: CancelConnect
) : ViewModel() {

    var state = mutableStateOf(HomeState())
        private set

    private val isConnecting get() = state.value.isConnecting

    fun buttonClick(open: (String) -> Unit){
        state.value = state.value.copy(isConnecting = !isConnecting)
        if(isConnecting) {
            connect(open)
        } else {
            cancelConnect()
        }
    }
    private fun connect(open: (String) -> Unit) {
        state.value = state.value.copy(connect = " connecting...", isConnecting = true)
        viewModelScope.launch {
            connectUser { exception ->
                if(exception != null) SnackBarManager.showMessage(exception.toSnackBarMessage())
                else {
                    state.value = state.value.copy(connect = "complete!!!")
//                    open(Screen.ChattingRoom.route)
                }
            }
        }
    }

    private fun cancelConnect(){
        state.value = state.value.copy(connect = "no connection", isConnecting = false)
        viewModelScope.launch {
            cancelConnect() {
                if(it != null) {
                    SnackBarManager.showMessage(AppText.cancelError)
                }
            }
        }
    }
}