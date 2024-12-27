package com.example.aihangman

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun UserRegistrationScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp, 16.dp, 300.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "AI Hangman",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(0.dp, 100.dp)
        )

        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Enter your name") }
        )
        Button(
            onClick = {
                userViewModel.registerUser(context, name.value)
                navController.navigate("leaderboard") {
                    popUpTo("registration") { inclusive = true }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Register")
        }
    }
}