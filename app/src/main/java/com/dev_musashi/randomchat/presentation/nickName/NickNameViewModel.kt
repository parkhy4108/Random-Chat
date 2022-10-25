package com.dev_musashi.randomchat.presentation.nickName

import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.usecases.CreateUserData
import com.dev_musashi.randomchat.domain.usecases.SignOut
import com.dev_musashi.randomchat.common.BottomBarScreen
import com.dev_musashi.randomchat.util.Resource
import com.dev_musashi.randomchat.util.SnackBarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NickNameViewModel @Inject constructor(
    private val createUserData: CreateUserData,
    private val signOut: SignOut
) : ViewModel() {

    var state = mutableStateOf(NickNameState())
        private set

    private val userNickName get() = state.value.userNickName
    private val userImage get() = state.value.userImage


    fun onChangedUserNickName(newValue: String) {
        state.value = state.value.copy(userNickName = newValue)
    }

    fun onChangedUserImage(newValue: String) {
        state.value = state.value.copy(userImage = newValue)
    }

    fun onConfirmedClick(navigateBottomBar: (String) -> Unit) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true)
            when (val userData = createUserData(
                User(
                    userName = userNickName,
                    userImage = userImage,
                    uid = ""
                )
            )) {
                is Resource.Success -> {
                    state.value = state.value.copy(isLoading = false)
                    navigateBottomBar(BottomBarScreen.Home.route)
                }
                is Resource.Error -> {
                    state.value = state.value.copy(isLoading = false)
                    SnackBarManager.showMessage(userData.message!!)
                }
            }
        }
    }

    fun onResetClick() {
        state.value = state.value.copy(userNickName = "", userImage = null)
    }

    fun onSignOutClick() {
        signOut()
    }
}
