package com.dev_musashi.randomchat.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.common.BottomBarScreen
import com.dev_musashi.randomchat.common.Screen
import com.dev_musashi.randomchat.domain.usecases.SignInWithEmailAndPassword
import com.dev_musashi.randomchat.domain.usecases.HasUserData
import com.dev_musashi.randomchat.domain.usecases.SignInWithCredential
import com.dev_musashi.randomchat.util.*
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmailAndPassword: SignInWithEmailAndPassword,
    private val signInWithCredential: SignInWithCredential,
    private val hasUserData: HasUserData
) : ViewModel() {

    var state = mutableStateOf(LoginState())
        private set

    private val userEmail get() = state.value.userEmail
    private val userPassword get() = state.value.userPassword

    fun onChangedUserEmail(newValue: String) {
        state.value = state.value.copy(userEmail = newValue)
    }

    fun onChangedUserPassword(newValue: String) {
        state.value = state.value.copy(userPassword = newValue)
    }

    fun onLoginClick(
        navigateBottomBar: (String) -> Unit,
        openAndPopUp: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            when(val signIn = signInWithEmailAndPassword(userEmail, userPassword)) {
                is Resource.Success -> {
                    checkData(openAndPopUp, navigateBottomBar)
                }
                is Resource.Error -> {
                    SnackBarManager.showMessage(signIn.message!!)
                }
            }
        }
    }

    fun signInWithGoogleToken(idToken: String, openAndPopUp: (String, String) -> Unit, navigateBottomBar: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            when(val signInGoogle = signInWithCredential(credential)) {
                is Resource.Success -> {
                    checkData(openAndPopUp, navigateBottomBar)
                }
                is Resource.Error -> {
                    SnackBarManager.showMessage(signInGoogle.message!!)
                }
            }
        }
    }

    fun onSignUpClick(open: (String) -> Unit) {
        open(Screen.SignUp.route)
    }

    private fun checkData(
        openAndPopUp: (String, String) -> Unit,
        navigateBottomBar: (String) -> Unit
    ) {
        viewModelScope.launch {
            when(val userData = hasUserData()) {
                is Resource.Success -> {
                    if(userData.data == true) {
                        navigateBottomBar(BottomBarScreen.Home.route)
                    } else {
                        openAndPopUp(Screen.NickName.route, Screen.Login.route)
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }

}