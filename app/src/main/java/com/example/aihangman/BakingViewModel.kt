package com.example.aihangman

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-8b",
        apiKey = BuildConfig.apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = Schema(
                name = "hangmanResponse",
                description = "Response containing hints for a given word",
                type = FunctionType.OBJECT,
                properties = mapOf(
                    "hints" to Schema(
                        name = "hints",
                        description = "A list of hints for the word",
                        type = FunctionType.ARRAY,
                        items = Schema(
                            name = "hint",
                            description = "A single hint for the word",
                            type = FunctionType.STRING
                        )
                    )
                ),
                required = listOf("hints")
            )
        }
    )

    private fun getRandomWord(context: Context): String {
        val inputStream = context.assets.open("slova.txt")
        val words = mutableListOf<String>()
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.forEachLine { line ->
                words.add(line)
            }
        }
        return words[Random.nextInt(words.size)]
    }

    fun sendPrompt(context: Context)
    {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val randomWord = getRandomWord(context)
                val response = generativeModel.generateContent(
                    content {
                        text("A random czech word that the user will be guessing in the Hangman game is: . " + randomWord +
                                ". Generate a list of three hints (in czech) that will help user to guess this word." +
                                "Order these hints from the most unclear one to the most obvious one." +
                                "The hints should not mention the structure of the word itself, nor the letters that it contains." +
                                "The hints should be as diverse as possible, so that they do not overlap in meaning." +
                                "The hints must not contain the word itself, nor any of its derivatives.")
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent + " " + randomWord)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}