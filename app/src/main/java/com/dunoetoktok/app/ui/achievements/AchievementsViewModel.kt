package com.dunoetoktok.app.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.ACHIEVEMENTS
import com.dunoetoktok.app.model.Achievement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AchievementUiModel(val achievement: Achievement, val isUnlocked: Boolean)

data class AchievementsUiState(
    val items: List<AchievementUiModel> = emptyList(),
    val unlockedCount: Int = 0,
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    gameRepository: GameRepository,
) : ViewModel() {

    val uiState: StateFlow<AchievementsUiState> = gameRepository.observePlayerStats()
        .map { stats ->
            val items = ACHIEVEMENTS.map { achievement -> AchievementUiModel(achievement, achievement.isUnlocked(stats)) }
            AchievementsUiState(items = items, unlockedCount = items.count { it.isUnlocked })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementsUiState())
}
