package com.example.aihangman

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun GameScreen(
    navController: NavController,
    gameViewModel: GameViewModel = viewModel()
) {
    var result by rememberSaveable { mutableStateOf("") }
    var errorCount by rememberSaveable { mutableStateOf(0) }
    val uiState by gameViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        onDispose { }
    }

    BasicTextField(
        value = "",
        onValueChange = {
            result += it
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .size(0.dp)
            .onGloballyPositioned {
                focusRequester.requestFocus()
            }
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Get a hint!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_assistant_24),
                contentDescription = stringResource(R.string.hint_icon),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        // Display an image of the hangman from drawable resources based on a errorCount
        val imageResId = when (errorCount) {
            0 -> R.drawable.hangman_0
            1 -> R.drawable.hangman_1
            2 -> R.drawable.hangman_2
            3 -> R.drawable.hangman_3
            4 -> R.drawable.hangman_4
            5 -> R.drawable.hangman_5
            6 -> R.drawable.hangman_6
            7 -> R.drawable.hangman_7
            8 -> R.drawable.hangman_8
            else -> R.drawable.hangman_8
        }
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = stringResource(R.string.hangman_image),
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = result
            )

            Row(
                modifier = Modifier.padding(all = 16.dp)
            ) {

                Button(
                    onClick = {
                        errorCount++
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = stringResource(R.string.action_go))
                }
            }

            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                var textColor = MaterialTheme.colorScheme.onSurface
                if (uiState is UiState.Error) {
                    textColor = MaterialTheme.colorScheme.error
                    result = (uiState as UiState.Error).errorMessage
                } else if (uiState is UiState.Success) {
                    textColor = MaterialTheme.colorScheme.onSurface
                    result = (uiState as UiState.Success).data
                }
                val scrollState = rememberScrollState()
                Text(
                    text = result,
                    textAlign = TextAlign.Start,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                )
            }
        }

    }


    // Display a loading indicator in the center of the screen
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom
    ) {
        // add a button to navigate to the game screen
        Button(
            onClick = {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(40.dp)
                .width(200.dp)
        ) {
            Text(text = "Open keyboard")
        }
    }
}