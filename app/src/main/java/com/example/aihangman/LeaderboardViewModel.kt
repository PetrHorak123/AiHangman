package com.example.aihangman

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<List<User>>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> =
        _uiState.asStateFlow()


    // Get leaderboard data from UserViewModel.getTopUsers()
    fun getLeaderboardData(field: String) {
        _uiState.value = UiState.Loading
        UserViewModel().getTopUsers(field) { users ->
            _uiState.value = UiState.Success(users)
        }
    }

}