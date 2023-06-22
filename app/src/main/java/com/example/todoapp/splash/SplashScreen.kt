package com.example.todoapp.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.destination.Home
import com.example.todoapp.destination.Splash
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    SplashScreenContent(navController)
}

@Composable
fun SplashScreenContent(navController: NavHostController) {
    Box(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentScale = ContentScale.FillBounds,
            contentDescription = "splash",
        )
    }
    LaunchedEffect(true){
        delay(4000)
        navController.navigate(Home.route){
            popUpTo(Splash.route){
                inclusive = true
            }
        }
    }
}