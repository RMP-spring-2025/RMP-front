package com.example.frontproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            painterResource(R.drawable.home),
            contentDescription = "home",
            modifier = Modifier.size(26.dp),
            tint = Color(0xff986ef2)
        )
        // Text on the screen
        Text(text = "Home", color = Color.Black)
    }
}

@Composable
fun GraphicsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            painterResource(R.drawable.icon_graphic),
            contentDescription = "home",
            modifier = Modifier.size(26.dp),
            tint = Color(0xff986ef2)
        )
        // Text on the screen
        Text(text = "Graphics", color = Color.Black)
    }
}

@Composable
fun BarCodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            painterResource(R.drawable.icon_camera),
            contentDescription = "home",
            modifier = Modifier.size(26.dp),
            tint = Color(0xff986ef2)
        )
        // Text on the screen
        Text(text = "Bar-Code", color = Color.Black)
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            painterResource(R.drawable.icon_profile),
            contentDescription = "home",
            modifier = Modifier.size(26.dp),
            tint = Color(0xff986ef2)
        )
        // Text on the screen
        Text(text = "Profile", color = Color.Black)
    }
}

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon on the screen
        Icon(
            painterResource(R.drawable.search),
            contentDescription = "home",
            modifier = Modifier.size(26.dp),
            tint = Color(0xff986ef2)
        )
        // Text on the screen
        Text(text = "Search", color = Color.Black)
    }
}