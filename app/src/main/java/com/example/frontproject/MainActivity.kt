package com.example.frontproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.frontproject.ui.components.screens.BarCodeScreen
import com.example.frontproject.ui.components.screens.GraphicsScreen
import com.example.frontproject.ui.components.screens.HomeScreen
import com.example.frontproject.ui.components.screens.ProfileScreen
import com.example.frontproject.ui.components.screens.SearchScreen
import com.example.frontproject.ui.theme.FrontProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontProjectTheme(dynamicColor = false, darkTheme = false) {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Surface(color = Color.White) {
        // Scaffold Component
        Scaffold(
            // Bottom navigation
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }, content = { padding ->
                // Nav host: where screens are placed
                NavHostContainer(navController = navController, padding = padding)
            }
        )
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("home") {
                HomeScreen()
            }

            composable("graphics") {
                GraphicsScreen()
            }

            composable("search") {
                SearchScreen()
            }

            composable("barCode") {
                BarCodeScreen()
            }

            composable("profile") {
                ProfileScreen()
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val centerItemIndex = 2

    Box(modifier = Modifier.fillMaxWidth()) {
        // Основной навбар
        NavigationBar(
            containerColor = Color(0xfff7f7f7),
            modifier = Modifier.fillMaxWidth()
        ) {
            Constants.BottomNavigationItems.forEachIndexed { index, navItem ->
                if (index == centerItemIndex) {
                    // Пустое место для центральной кнопки
                    // Нужно так как навбар обрезает содержимое по своему размеру
                    // по хорошему надо наверное переписать на более адекватное решение
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = { navController.navigate(navItem.route) },
                        icon = { },
                        label = { },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                } else {
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = { navController.navigate(navItem.route) },
                        icon = {
                            Icon(
                                painterResource(navItem.icon),
                                contentDescription = navItem.label,
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "•",
                                color = Color(0xff986ef2),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xff986ef2),
                            unselectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            indicatorColor = Color(0xFFf7f7f7)
                        )
                    )
                }
            }
        }

        // Центральная кнопка поверх навбара
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-20).dp)
                    .background(
                        color = Color(0xff986ef2),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(Constants.BottomNavigationItems[centerItemIndex].icon),
                    contentDescription = Constants.BottomNavigationItems[centerItemIndex].label,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun MyAppPreview() {
    MyApp()
}