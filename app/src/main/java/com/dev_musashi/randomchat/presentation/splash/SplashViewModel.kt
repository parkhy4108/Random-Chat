package com.dev_musashi.randomchat.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.usecases.CurrentUser
import com.dev_musashi.randomchat.domain.usecases.HasNickName
import com.dev_musashi.randomchat.domain.usecases.Online
import com.dev_musashi.randomchat.util.BottomBarScreen
import com.dev_musashi.randomchat.util.Screen
import com.dev_musashi.randomchat.util.SnackBarManager
import com.dev_musashi.randomchat.util.SnackBarMessage.Companion.toSnackBarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val currentUser: CurrentUser,
    private val hasNickName: HasNickName,
    private val statusOnline: Online
) : ViewModel() {

    fun hasUser(
        navigateBottomBar: (String)->Unit,
        openAndPopUp: (String, String) -> Unit,
    ) {
        if(currentUser()) {
            viewModelScope.launch {
                hasNickName(
                    onError = { exception -> SnackBarManager.showMessage(exception.toSnackBarMessage())}
                ) {
                    if (it) {
                        statusOnline()
                        navigateBottomBar(BottomBarScreen.Home.route)
                    }
                    else {
                        openAndPopUp(Screen.NickName.route, Screen.Splash.route)
                    }
                }
            }
        }else {
            openAndPopUp(Screen.Login.route, Screen.Splash.route)
        }
    }

    fun statusOnline() {
        viewModelScope.launch {
            statusOnline {
                if (it != null) {
                    SnackBarManager.showMessage(it.toSnackBarMessage())
                }
            }
        }
    }
}

