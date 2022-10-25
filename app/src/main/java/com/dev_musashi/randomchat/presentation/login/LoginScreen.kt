package com.dev_musashi.randomchat.presentation.login

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev_musashi.randomchat.util.addFocusCleaner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.dev_musashi.randomchat.R.drawable as AppImg
import com.dev_musashi.randomchat.R.string as AppText

@Composable
fun LoginScreen(
    open: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    navigateBottomBar: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val token = stringResource(id = AppText.default_web_client_id)
    val context = LocalContext.current
    val state by viewModel.state
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    val focusManager = LocalFocusManager.current
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, signInOptions)
    val launcher = registerGoogleActivityResultLauncher(viewModel, openAndPopUp, navigateBottomBar)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .addFocusCleaner(focusManager),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Icon(
            painter = painterResource(id = AppImg.ic_send),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "RandomChat", fontSize = 30.sp, color = Color.White)
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            value = state.userEmail,
            onValueChange = { viewModel.onChangedUserEmail(it) },
            placeholder = { Text(text = "이메일", color = Color.White) },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White)
                .focusRequester(focusRequester)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color((0xFF1B1B1A)),
                textColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = state.userPassword,
            onValueChange = { viewModel.onChangedUserPassword(it) },
            placeholder = { Text(text = "비밀번호", color = Color.White) },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White)
                .focusRequester(focusRequester)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color((0xFF1B1B1A)),
                textColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            modifier = Modifier.width(250.dp),
            onClick = { viewModel.onLoginClick(navigateBottomBar,openAndPopUp) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Text(text = "Login", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            modifier = Modifier
                .width(250.dp),
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = AppImg.ic_google),
                    contentDescription = null ,
                    modifier = Modifier.size(18.dp)
                )
                Text(text = "Sign in with Google", color = Color(0x8A000000))
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            modifier = Modifier.width(250.dp),
            onClick = { viewModel.onSignUpClick(open) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Text(text = "SignUp", color = Color.Gray)
        }
    }
}

@Composable
fun registerGoogleActivityResultLauncher(
    viewModel: LoginViewModel,
    openAndPopUp: (String, String) -> Unit,
    navigateBottomBar: (String) -> Unit,
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            viewModel.signInWithGoogleToken(account.idToken!!, openAndPopUp, navigateBottomBar)
        } catch (e: ApiException) {
            Log.w("TAG", "Google sign in failed", e)
        }
    }
}