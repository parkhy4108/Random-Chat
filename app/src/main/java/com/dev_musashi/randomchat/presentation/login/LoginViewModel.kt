package com.dev_musashi.randomchat.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.usecases.SignInWithEmailAndPassword
import com.dev_musashi.randomchat.domain.usecases.HasNickName
import com.dev_musashi.randomchat.domain.usecases.SignInWithCredential
import com.dev_musashi.randomchat.util.BottomBarScreen
import com.dev_musashi.randomchat.util.Screen
import com.dev_musashi.randomchat.util.SnackBarManager
import com.dev_musashi.randomchat.util.SnackBarMessage.Companion.toSnackBarMessage
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmailAndPassword: SignInWithEmailAndPassword,
    private val signInWithCredential: SignInWithCredential,
    private val hasNickName: HasNickName
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
            signInWithEmailAndPassword(userEmail, userPassword) { exception ->
                if (exception != null) {
                    SnackBarManager.showMessage(exception.toSnackBarMessage())
                } else {
                    hasNickName(openAndPopUp, navigateBottomBar)
                }
            }
        }
    }

    fun signInWithGoogleToken(idToken: String, openAndPopUp: (String, String) -> Unit, navigateBottomBar: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            signInWithCredential(credential) { exception ->
                if (exception != null) {
                    SnackBarManager.showMessage(exception.toSnackBarMessage())
                } else {
                    hasNickName(openAndPopUp, navigateBottomBar)
                }
            }
        }
    }

    fun onSignUpClick(open: (String) -> Unit) {
        open(Screen.SignUp.route)
    }

    private fun hasNickName(
        openAndPopUp: (String, String) -> Unit,
        navigateBottomBar: (String) -> Unit
    ) {
        viewModelScope.launch {
            hasNickName(
                onError = { exception -> SnackBarManager.showMessage(exception.toSnackBarMessage()) }
            ) {
                if (it) navigateBottomBar(BottomBarScreen.Home.route)
                else openAndPopUp(Screen.NickName.route, Screen.Splash.route)
            }
        }
    }


}