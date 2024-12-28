package com.example.aihangman

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun GameScreen(
    navController: NavController,
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    //game
    var input by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    var word by rememberSaveable { mutableStateOf("") }
    var errorCount by rememberSaveable { mutableStateOf(0) }

    //hints
    var hints by rememberSaveable { mutableStateOf(listOf<String>()) }
    var hintIndex by rememberSaveable { mutableStateOf(0) }
    var isSnackbarVisible by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        onDispose { }
    }

    LaunchedEffect (Unit){
        word = gameViewModel.getRandomWord(context)
        // Display the word in the console
        println(word)

        // Set result variable with number of underscores based on the length of the word
        result = "_".repeat(word.length)
    }

    BasicTextField(
        value = "",
        onValueChange = {
            println(it)

            input += it
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .size(0.dp)
            .onGloballyPositioned {
                focusRequester.requestFocus()
            }
    )

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(top = 50.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (uiState is UiState.Initial) {
                        Text(
                            text = "Get a hint!",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                    } else if (uiState is UiState.Loading) {
                        Text(
                            text = "Generating a hint...",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                    } else if (uiState is UiState.Success) {
                        Text(
                            text = "Used ${hintIndex}/${hints.size} hints",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )

                        val jsonObject = JSONObject((uiState as UiState.Success).data)
                        val hintsArray = jsonObject.getJSONArray("hints")
                        val hintsList = mutableListOf<String>()
                        for (i in 0 until hintsArray.length()) {
                            hintsList.add(hintsArray.getString(i))
                        }
                        hints = hintsList

                        //Display the first hint in a snackbar
                        LaunchedEffect(hints) {
                            if (hints.isNotEmpty()) {
                                isSnackbarVisible = true
                                snackbarHostState.showSnackbar(hints[hintIndex])
                                isSnackbarVisible = false
                                hintIndex++
                            }
                        }
                    }


                    Icon(
                        painter = painterResource(id = R.drawable.baseline_assistant_24),
                        contentDescription = stringResource(R.string.hint_icon),
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = !isSnackbarVisible) {

                                if (hints.isEmpty() && uiState is UiState.Initial) {
                                    gameViewModel.getHints(context, word)
                                }else if (uiState is UiState.Success){
                                    if (hintIndex < hints.size) {
                                        // Display a snackbar with the next hint
                                        coroutineScope.launch {
                                            isSnackbarVisible = true
                                            snackbarHostState.showSnackbar(hints[hintIndex])
                                            isSnackbarVisible = false
                                            hintIndex++
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            isSnackbarVisible = true
                                            snackbarHostState.showSnackbar("No more hints available")
                                            isSnackbarVisible = false
                                            errorCount++
                                        }
                                    }
                                }
                            }
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
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = result.toCharArray().joinToString(" "),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineLarge
                )
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
    }


}