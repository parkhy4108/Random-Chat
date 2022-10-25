package com.dev_musashi.randomchat.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev_musashi.randomchat.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    openAndPopUp: (String, String)->Unit,
    navigateBottomBar: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
){
    var startAnimation by remember {
        mutableStateOf(false)
    }
    val alphaAnim = animateFloatAsState(
        targetValue =  if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        viewModel.hasUser(navigateBottomBar, openAndPopUp)
    }
    Splash(alpha = alphaAnim.value)
}

@Composable
fun Splash(alpha: Float) {
    Box(
        modifier = Modifier
            .background(Color(0xFF1B1B1A))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha),
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "Logo",
                tint = Color.White
            )
            Text(
                text = "Random Chat" ,
                color = Color.White,
                modifier = Modifier.alpha(alpha),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive

            )
        }

    }
}
