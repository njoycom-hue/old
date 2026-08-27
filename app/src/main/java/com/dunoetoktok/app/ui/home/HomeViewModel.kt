package com.dunoetoktok.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.util.ExperienceCalculator
import com.dunoetoktok.app.util.PlayerLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val bestScores: Map<GameType, Int?> = emptyMap(),
    val level: PlayerLevel = ExperienceCalculator.levelForTotalXp(0),
    val currentStreak: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    gameRepository: GameRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = gameRepository.observePlayerStats()
        .map { stats ->
            HomeUiState(
                bestScores = stats.bestScores,
                level = ExperienceCalculator.levelForTotalXp(stats.totalXp),
                currentStreak = stats.currentStreak,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
