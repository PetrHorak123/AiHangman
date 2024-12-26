package com.example.aihangman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aihangman.ui.theme.AiHangmanTheme
import com.google.firebase.FirebaseApp
import android.provider.Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            AiHangmanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    // If current device id is not in database then navigate to registration screen
                    AppNavigation()

                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel()

    LaunchedEffect(Unit) {
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        userViewModel.getUserByDeviceId(deviceId) { user ->
            if (user != null) {
                navController.navigate("leaderboard") {
                    popUpTo("loading") { inclusive = true }
                }
            } else {
                navController.navigate("registration") {
                    popUpTo("loading") { inclusive = true }
                }
            }
        }
    }
    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") { LoadingScreen() }
        composable("registration") { UserRegistrationScreen(navController) }
        composable("leaderboard") { LeaderboardScreen(navController) }
        composable("game") { GameScreen(navController) }
    }
}