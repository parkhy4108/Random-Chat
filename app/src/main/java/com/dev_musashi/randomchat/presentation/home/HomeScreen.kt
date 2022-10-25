package com.dev_musashi.randomchat.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev_musashi.randomchat.R.drawable as AppImg

@Composable
fun HomeScreen(
    open: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.state
//    val permissionLauncher= rememberLauncherForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) {
//            viewModel.loadCurrentWeather()
//        }
//    LaunchedEffect(Unit) {
//        permissionLauncher.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ))
//    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = state.connect, color = Color.White, fontSize = 30.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            elevation = ButtonDefaults.elevation(10.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White),
            modifier = Modifier
                .size(150.dp),
            onClick = { viewModel.buttonClick(open) }
        ) {
            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = AppImg.ic_send),
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "connect user",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.SansSerif
        )


    }
}