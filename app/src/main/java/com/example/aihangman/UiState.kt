package com.example.aihangman

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState<out T> {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState<Nothing>

    /**
     * Still loading
     */
    object Loading : UiState<Nothing>

    /**
     * Text has been generated
     */
    data class Success<out T>(val data: T) : UiState<T>

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState<Nothing>
}