package com.dev_musashi.randomchat.presentation.signUp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.domain.usecases.CreateUserAuth
import com.dev_musashi.randomchat.util.SnackBarManager
import com.dev_musashi.randomchat.util.SnackBarMessage.Companion.toSnackBarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.dev_musashi.randomchat.R.string as AppText
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val createUserAuth: CreateUserAuth
) : ViewModel() {

    var state = mutableStateOf(SignUpState())
        private set

    private val userEmail get() = state.value.userEmail
    private val userPassword get() = state.value.userPassword

    fun onChangedUserEmail(newValue: String) {
        state.value = state.value.copy(userEmail = newValue)
    }

    fun onChangedUserPassword(newValue: String) {
        state.value = state.value.copy(userPassword = newValue)
    }

    fun onSignUpClick(popUp: () -> Unit) {
        if(userEmail == "" || userPassword == "") {
            SnackBarManager.showMessage(AppText.NoEmpty)
        }
        else {
            viewModelScope.launch {
                createUserAuth(
                    userEmail = userEmail,
                    userPassword = userPassword
                ) { exception ->
                    if(exception != null) {
                        SnackBarManager.showMessage(exception.toSnackBarMessage())
                    }
                    else {
                        SnackBarManager.showMessage(AppText.signUpOkay)
                        popUp()
                    }
                }
            }
        }
    }

}