package com.dunoetoktok.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(val bestScores: Map<GameType, Int?> = emptyMap())

@HiltViewModel
class HomeViewModel @Inject constructor(
    gameRepository: GameRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        GameType.entries.map { gameType ->
            gameRepository.observeBestScore(gameType).map { score -> gameType to score }
        }
    ) { pairs -> HomeUiState(bestScores = pairs.toMap()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
