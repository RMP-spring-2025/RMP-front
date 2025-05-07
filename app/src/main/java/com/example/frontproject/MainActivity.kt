package com.example.frontproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.frontproject.ui.components.screens.AuthScreen
import com.example.frontproject.ui.components.screens.BarCodeScreen
import com.example.frontproject.ui.components.screens.CaloriesScreen
import com.example.frontproject.ui.components.screens.CreateProfileScreen
import com.example.frontproject.ui.components.screens.GraphicsScreen
import com.example.frontproject.ui.components.screens.HomeScreen
import com.example.frontproject.ui.components.screens.ProfileScreen
import com.example.frontproject.ui.components.screens.RegistrationScreen
import com.example.frontproject.ui.components.screens.SearchScreen
import com.example.frontproject.ui.components.screens.SettingsScreen
import com.example.frontproject.ui.components.screens.SuccessRegistrationScreen
import com.example.frontproject.ui.theme.FrontProjectTheme
import com.example.frontproject.ui.viewmodel.AuthState
import com.example.frontproject.ui.viewmodel.CreateUserState
import com.example.frontproject.ui.viewmodel.RegisterState
import com.example.frontproject.ui.viewmodel.SettingsViewModel
import com.example.frontproject.ui.viewmodel.SettingsViewModelFactory
import com.example.frontproject.ui.components.screens.WelcomeScreen

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
    val context = LocalContext.current
    val appContainer = (context.applicationContext as RmpApplication).appContainer

    // Создаем ViewModel с помощью Factory
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            settingsRepository = appContainer.settingsRepository,
            tokenRepository = appContainer.tokenRepository,
            apiRequestExecutor = appContainer.apiRequestExecutor
        )
    )

    // Определяем начальный маршрут на основе наличия токенов
    val tokenRepository = appContainer.tokenRepository
    val initialStartDestination = remember {
        if (tokenRepository.getCurrentAccessToken().isNullOrEmpty()) {
            "welcome"
        } else {
            "home" // Если токен есть, сразу на главный экран
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in listOf(
        "home",
        "calories",
        "graphics",
        "search",
        "barCode",
        "profile",
        "settings",
        "welcome"
    )

    Surface(color = Color.White) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            },
            content = { padding ->
                NavHostContainer(
                    navController = navController,
                    padding = padding,
                    settingsViewModel = settingsViewModel,
                    startDestination = initialStartDestination // Передаем начальный маршрут
                )
            }
        )
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    settingsViewModel: SettingsViewModel,
    startDestination: String // Принимаем начальный маршрут
) {
    // Отслеживаем состояния авторизации для навигации
    val registerState by settingsViewModel.registerState.collectAsState()
    val createUserState by settingsViewModel.createUserState.collectAsState()
    val authState by settingsViewModel.authState.collectAsState()

    // Обрабатываем навигацию после успешной регистрации
    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate("create_profile") {
                // Очищаем бэкстек до welcome, чтобы пользователь не мог вернуться на экраны регистрации
                popUpTo("welcome") { inclusive = true }
            }
            settingsViewModel.resetRegisterState()
        }
    }

    // Обрабатываем навигацию после создания профиля
    LaunchedEffect(createUserState) {
        if (createUserState is CreateUserState.Success) {
            navController.navigate("success"){
                popUpTo("welcome") { inclusive = true }
            }
            settingsViewModel.resetCreateUserState()
        }
    }

    // Обрабатываем навигацию после входа
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true } // Очищаем бэкстек до welcome
            }
            settingsViewModel.resetAuthState()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination, // Используем переданный начальный маршрут
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            // Экран приветствия
            composable("welcome") {
                WelcomeScreen(
                    onRegisterClick = { navController.navigate("register") },
                    onLoginClick = { navController.navigate("login") }
                )
            }

            // Экран регистрации
            composable("register") {
                RegistrationScreen(
                    settingsViewModel = settingsViewModel,
                    onLoginClick = { navController.navigate("login") }
                )
            }

            // Экран создания профиля
            composable("create_profile") {
                CreateProfileScreen(
                    settingsViewModel = settingsViewModel
                )
            }

            // Экран успешной регистрации
            composable("success") {
                // Предполагаем, что имя пользователя можно получить из ViewModel или другого источника
                val userName = remember {
                    // Здесь можно добавить логику получения имени пользователя, если оно доступно
                    // Например, из userProfileState во ViewModel
                    "Пользователь"
                }
                SuccessRegistrationScreen(
                    userName = userName,
                    onGoHomeClicked = {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }

            // Экран авторизации
            composable("login") {
                AuthScreen(
                    settingsViewModel = settingsViewModel,
                    onRegisterClick = { navController.navigate("register") },
                    // onLoginSuccess уже обрабатывается в LaunchedEffect(authState)
                )
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable(
                route = "calories",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(durationMillis = 500)
                    )
                }
            ) {
                CaloriesScreen(navController)
            }

            composable("graphics") {
                GraphicsScreen()
            }

            composable("search") {
                BarCodeScreen(navController)
            }

            composable("barCode") {
                SearchScreen()
            }

            composable("profile") {
                ProfileScreen(navController)
            }

            composable("settings") {
                SettingsScreen(navController)
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