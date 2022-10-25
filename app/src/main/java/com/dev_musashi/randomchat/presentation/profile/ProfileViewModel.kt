package com.dev_musashi.randomchat.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.usecases.ChangeUserData
import com.dev_musashi.randomchat.domain.usecases.GetUserData
import com.dev_musashi.randomchat.domain.usecases.SignOut
import com.dev_musashi.randomchat.util.BottomBarScreen
import com.dev_musashi.randomchat.util.Screen
import com.dev_musashi.randomchat.util.SnackBarManager
import com.dev_musashi.randomchat.util.SnackBarMessage.Companion.toSnackBarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserData: GetUserData,
    private val changeUserData: ChangeUserData,
    private val signOut: SignOut
) : ViewModel() {

    var state = mutableStateOf(ProfileState())
        private set

    private val userNickName get() = state.value.userNickName
    private val userImage get() = state.value.userImage
    private var oldNickName = ""

    fun userData() {
        viewModelScope.launch {
            getUserData(
                onError = { SnackBarManager.showMessage(it.toSnackBarMessage()) }
            ) {
                state.value = state.value.copy(nickName = it.userName, userImage = it.userImage.toString())
                oldNickName = it.userName
            }
        }
    }

    fun onChangedUserNickName(newValue: String) {
        state.value = state.value.copy(userNickName = newValue)
    }

    fun onChangedUserImage(newValue: String) {
        state.value = state.value.copy(userImage = newValue)
    }

    fun onConfirmedClick() {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true)
            changeUserData(
                user = User(
                    userName = userNickName,
                    userImage = userImage?.toUri(),
                    uid = "",
                )
            ) { exception ->
                if(exception != null) {
                    state.value = state.value.copy(isLoading = false)
                    SnackBarManager.showMessage(exception.toSnackBarMessage())
                }else {
                    state.value = state.value.copy(nickName = userNickName, isLoading = false)
                    oldNickName = userNickName
                }
            }
        }
    }

    fun onResetClick() {
        state.value = state.value.copy(userNickName = "", userImage = null)
    }

    fun onSignOutClick(openAndPopUp: (String, String)-> Unit) {
        signOut()
        openAndPopUp(Screen.Login.route, BottomBarScreen.Profile.route)
    }
}