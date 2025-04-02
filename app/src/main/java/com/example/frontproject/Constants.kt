package com.example.frontproject

object Constants {
    val BottomNavigationItems = listOf(
        // Home screen
        BottomNavigationItem(
            label = "Home",
            icon = R.drawable.home,
            route = "home"
        ),
        // Graphics screen
        BottomNavigationItem(
            label = "Graphics",
            icon = R.drawable.activity,
            route = "graphics"
        ),
        // Search screen
        BottomNavigationItem(
            label = "Search",
            icon = R.drawable.search,
            route = "search"
        ),
        // Bar-Code screen
        BottomNavigationItem(
            label = "BarCode",
            icon = R.drawable.camera,
            route = "barCode"
        ),
        // Profile screen
        BottomNavigationItem(
            label = "Profile",
            icon = R.drawable.profile,
            route = "profile"
        )
    )
}