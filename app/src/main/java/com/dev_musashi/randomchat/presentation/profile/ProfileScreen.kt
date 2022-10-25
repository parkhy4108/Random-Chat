package com.dev_musashi.randomchat.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev_musashi.randomchat.R
import com.dev_musashi.randomchat.util.FitImgLoad
import com.dev_musashi.randomchat.util.addFocusCleaner

@Composable
fun ProfileScreen(
    openAndPopUp: (String, String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    val focusManager = LocalFocusManager.current

    val selectImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                viewModel.onChangedUserImage(it.toString())
            }
        }

    LaunchedEffect(Unit) {
        viewModel.userData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .addFocusCleaner(focusManager)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onResetClick()
                }) {
                Text(text = "초기화", color = Color.White)
            }
            TextButton(
                enabled = state.userNickName != "",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onConfirmedClick()
                }
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp),
                        color = MaterialTheme.colors.primary,
                        strokeWidth = 2.dp
                    )
                } else{
                    Text(text = "확인", color = Color.White)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.BottomEnd
            ) {
                FitImgLoad(
                    imgUrl = state.userImage,
                    modifier = Modifier
                        .clickable { selectImageLauncher.launch("image/*") }
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            state.nickName?.let { Text(text = it, color = Color.White) }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = state.userNickName,
                onValueChange = { viewModel.onChangedUserNickName(it) },
                placeholder = { Text(text = "닉네임을 입력하세요", color = Color.White) },
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
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextButton(
                onClick = {
                    viewModel.onSignOutClick(openAndPopUp)
                }
            ) {
                Text(text = "로그아웃", color = Color.White)
            }
        }
    }
}