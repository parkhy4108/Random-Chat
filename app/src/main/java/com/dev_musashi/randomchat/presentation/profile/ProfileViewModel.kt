package com.dev_musashi.randomchat.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.usecases.ChangeUserImage
import com.dev_musashi.randomchat.domain.usecases.GetUserData
import com.dev_musashi.randomchat.domain.usecases.SignOut
import com.dev_musashi.randomchat.common.BottomBarScreen
import com.dev_musashi.randomchat.util.Resource
import com.dev_musashi.randomchat.common.Screen
import com.dev_musashi.randomchat.domain.usecases.ChangeUserNickName
import com.dev_musashi.randomchat.util.SnackBarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserData: GetUserData,
    private val changeUserImage: ChangeUserImage,
    private val changeUserNickName: ChangeUserNickName,
    private val signOut: SignOut
) : ViewModel() {

    var state = mutableStateOf(ProfileState())
        private set

    private val userNickName get() = state.value.userNickName
    private val userImage get() = state.value.userImage
    var oldNickName = ""
    var oldImage = ""

    fun userData() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value = state.value.copy(isUserLoading = true)
            when (val userData = getUserData()) {
                is Resource.Success -> {
                    state.value = state.value.copy(isUserLoading = false)
                    state.value = state.value.copy(
                        nickName = userData.data?.userName,
                        userImage = userData.data?.userImage
                    )
                    oldNickName = userData.data?.userName!!
                    oldImage = userData.data.userImage!!
                }
                is Resource.Error -> {
                    state.value = state.value.copy(isUserLoading = false)
                }
            }
        }
    }

    fun onChangedUserNickName(newValue: String) {
        state.value = state.value.copy(userNickName = newValue)
    }

    fun onChangedUserImage(newValue: String) {
        state.value = state.value.copy(userImage = newValue)
    }

//    fun onConfirmedClick() {
//        viewModelScope.launch(Dispatchers.IO) {
//            when (val changeUserData = changeUserData(
//                user = User(
//                    userName = userNickName,
//                    userImage = userImage,
//                    uid = "",
//                )
//            )) {
//                is Resource.Loading -> {
//                    state.value = state.value.copy(isLoading = true)
//                }
//                is Resource.Success -> {
//                    state.value = state.value.copy(nickName = userNickName, isLoading = false)
//                    oldNickName = userNickName
//                }
//                is Resource.Error -> {
//                    state.value = state.value.copy(isLoading = false)
//                    SnackBarManager.showMessage(changeUserData.message!!)
//                }
//            }
//        }
//    }

    fun onConfirmedClick() {
        viewModelScope.launch(Dispatchers.IO) {
            if(userImage != oldImage && userNickName != oldNickName) {
                changeBoth(userImage = userImage, userNickName = userNickName)
            } else {
                if(userImage != oldImage) {
                    changeProfileImage(userImage = userImage)
                } else {
                    changeNickName(userNickName = userNickName)
                }
            }
        }
    }

    private fun changeNickName(userNickName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state.value = state.value.copy(isLoading = true)
            when(val changeNickName = changeUserNickName(userNickName)){
                is Resource.Success -> {
                    oldNickName = userNickName
                    state.value = state.value.copy(nickName = userNickName, isLoading = false)
                }
                is Resource.Error -> {
                    state.value = state.value.copy(isLoading = false)
                    SnackBarManager.showMessage(changeNickName.message!!)
                }
            }
        }
    }

    private fun changeProfileImage(userImage: String?){
        viewModelScope.launch(Dispatchers.IO) {
            state.value = state.value.copy(isLoading = true)
            when(val changeImage = changeUserImage(userImage)){
                is Resource.Success -> {
                    state.value = state.value.copy(isLoading = false)

                }
                is Resource.Error -> {
                    state.value = state.value.copy(isLoading = false)
                    SnackBarManager.showMessage(changeImage.message!!)
                }
            }
        }
    }

    private fun changeBoth(userImage: String?, userNickName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state.value = state.value.copy(isLoading = true)
            when(val changeImage = changeUserImage(userImage)){
                is Resource.Success -> {
                    state.value = state.value.copy(isLoading = true)
                    changeUserNickName(userNickName)
                }
                is Resource.Error -> {
                    state.value = state.value.copy(isLoading = false)
                    SnackBarManager.showMessage(changeImage.message!!)
                }
            }
        }
    }

    fun onResetClick() {
        state.value = state.value.copy(userNickName = "", userImage = null)
    }

    fun onSignOutClick(openAndPopUp: (String, String) -> Unit) {
        signOut()
        openAndPopUp(Screen.Login.route, BottomBarScreen.Profile.route)
    }
}