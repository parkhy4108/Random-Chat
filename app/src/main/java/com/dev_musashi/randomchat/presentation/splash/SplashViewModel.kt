package com.dev_musashi.randomchat.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.usecases.CurrentUser
import com.dev_musashi.randomchat.domain.usecases.HasUserData
import com.dev_musashi.randomchat.domain.usecases.Online
import com.dev_musashi.randomchat.common.BottomBarScreen
import com.dev_musashi.randomchat.util.Resource
import com.dev_musashi.randomchat.common.Screen
import com.dev_musashi.randomchat.util.SnackBarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val currentUser: CurrentUser,
    private val hasUserData: HasUserData,
    private val statusOnline: Online
) : ViewModel() {

    fun hasUser(
        navigateBottomBar: (String)->Unit,
        openAndPopUp: (String, String) -> Unit,
    ) {
        if(currentUser()) {
            viewModelScope.launch {
                when(val userData = hasUserData()) {
                    is Resource.Success -> {
                        if(userData.data == true) {
                            online()
                            navigateBottomBar(BottomBarScreen.Home.route)
                        } else {
                            openAndPopUp(Screen.NickName.route, Screen.Splash.route)
                        }
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }else {
            openAndPopUp(Screen.Login.route, Screen.Splash.route)
        }
    }

    private fun online(){
        viewModelScope.launch {
            val status = statusOnline()
            if(status.message != null) {
                SnackBarManager.showMessage(status.message)
            }
        }
    }

}

