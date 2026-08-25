package com.dunoetoktok.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.GameResult
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.model.formatBestScoreText
import com.dunoetoktok.app.util.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val RECENT_RESULTS_LIMIT = 5

data class GameHistorySection(
    val gameType: GameType,
    val bestScoreText: String,
    val recentResults: List<GameResult>,
)

data class StatsUiState(
    val streakDays: Int = 0,
    val sections: List<GameHistorySection> = emptyList(),
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    gameRepository: GameRepository,
) : ViewModel() {

    private val streakFlow = gameRepository.observePlayedDates()
        .map { StreakCalculator.currentStreak(it) }

    private val sectionsFlow = combine(
        GameType.entries.map { gameType ->
            combine(
                gameRepository.observeBestScore(gameType),
                gameRepository.observeRecentResults(gameType, RECENT_RESULTS_LIMIT),
            ) { best, recent ->
                GameHistorySection(
                    gameType = gameType,
                    bestScoreText = gameType.formatBestScoreText(best),
                    recentResults = recent,
                )
            }
        }
    ) { it.toList() }

    val uiState: StateFlow<StatsUiState> = combine(streakFlow, sectionsFlow) { streak, sections ->
        StatsUiState(streakDays = streak, sections = sections)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}
