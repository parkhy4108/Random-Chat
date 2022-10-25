package com.dev_musashi.randomchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev_musashi.randomchat.data.repositoryImpl.region
import com.dev_musashi.randomchat.presentation.chattingList.ListScreen
import com.dev_musashi.randomchat.util.Screen
import com.dev_musashi.randomchat.presentation.home.HomeScreen
import com.dev_musashi.randomchat.presentation.login.LoginScreen
import com.dev_musashi.randomchat.presentation.nickName.NickNameScreen
import com.dev_musashi.randomchat.presentation.profile.ProfileScreen
import com.dev_musashi.randomchat.presentation.signUp.SignUpScreen
import com.dev_musashi.randomchat.presentation.splash.SplashScreen
import com.dev_musashi.randomchat.ui.theme.RandomChatTheme
import com.dev_musashi.randomchat.util.BottomBarScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RandomChatTheme {
                val appState = rememberAppState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        bottomBar = {
                            if (appState.shouldShowBottomBar) {
                                BottomNavigationBar(
                                    tabs = appState.bottomBarTabs,
                                    currentRoute = appState.currentRoute!!,
                                    navigateToRoute = appState::navigateToBottomBarRoute,
                                    modifier = Modifier
                                )
                            }
                        },
                        snackbarHost = {
                            SnackbarHost(
                                hostState = it,
                                modifier = Modifier,
                                snackbar = { snackBarData ->
                                    Snackbar(snackBarData)
                                }
                            )
                        },
                        scaffoldState = appState.scaffoldState
                    ) { innerPadding ->
                        NavHost(
                            navController = appState.navController,
                            startDestination = Screen.Splash.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            graph(appState = appState)
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val notConnect = FirebaseDatabase.getInstance(region).reference
            .child("status").child("notconnect").child(uid)
        notConnect.onDisconnect().removeValue()
    }


}

fun NavGraphBuilder.graph(appState: AppState) {
    composable(route = Screen.Splash.route) {
        SplashScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            navigateBottomBar = { route -> appState.startBottomBarDestination(route) }
        )
    }
    composable(route = Screen.Login.route) {
        LoginScreen(
            open = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            navigateBottomBar = { route -> appState.startBottomBarDestination(route) },
        )
    }
    composable(route = Screen.SignUp.route) {
        SignUpScreen(
            popUp = { appState.popUp() }
        )
    }
    composable(route = Screen.NickName.route) {
        NickNameScreen(navigateBottomBar = { route -> appState.startBottomBarDestination(route) })
    }
    composable(route = BottomBarScreen.Home.route) {
        HomeScreen(open = { route, -> appState.navigate(route) })
    }
    composable(route = BottomBarScreen.ChattingList.route) {
        ListScreen()
    }
    composable(route = BottomBarScreen.Profile.route) {
        ProfileScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
}

@Composable
fun BottomNavigationBar(
    tabs: List<BottomBarScreen>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier
) {
    val currentSection = tabs.first { it.route == currentRoute }

    BottomNavigation(
        modifier = modifier
            .height(45.dp)
            .graphicsLayer {
                shape = RectangleShape
                clip = true
            },
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        tabs.forEach { section ->
            val selected = section == currentSection
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null
                    )
                },
                label = null,
                selected = selected,
                unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
                onClick = { navigateToRoute(section.route) },
            )
        }
    }
}

