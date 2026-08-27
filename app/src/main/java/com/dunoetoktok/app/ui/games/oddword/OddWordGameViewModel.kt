package com.dunoetoktok.app.ui.games.oddword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.Achievement
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.model.PlayerStats
import com.dunoetoktok.app.model.findNewlyUnlockedAchievements
import com.dunoetoktok.app.util.OddWordGenerator
import com.dunoetoktok.app.util.OddWordQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val COMBO_THRESHOLD = 3

data class OddWordGameUiState(
    val questionIndex: Int = 0,
    val question: OddWordQuestion? = null,
    val correctCount: Int = 0,
    val answerStreak: Int = 0,
    val selectedChoice: String? = null,
    val resultMessage: String = "",
    val isComplete: Boolean = false,
    val isNewRecord: Boolean = false,
    val newlyUnlockedAchievements: List<Achievement> = emptyList(),
)

@HiltViewModel
class OddWordGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OddWordGameUiState())
    val uiState: StateFlow<OddWordGameUiState> = _uiState.asStateFlow()

    private var previousBest: Int? = null
    private var statsBeforeGame: PlayerStats? = null
    private var isLocked = false

    init {
        startNewGame()
    }

    fun startNewGame() {
        isLocked = false
        _uiState.value = OddWordGameUiState(question = OddWordGenerator.generate())
        viewModelScope.launch {
            previousBest = gameRepository.observeBestScore(GameType.ODD_WORD).first()
            statsBeforeGame = gameRepository.observePlayerStats().first()
        }
    }

    fun onChoiceSelected(choice: String) {
        val state = _uiState.value
        val question = state.question
        if (isLocked || state.isComplete || question == null) return
        isLocked = true

        val isCorrect = choice == question.answer
        val newStreak = if (isCorrect) state.answerStreak + 1 else 0
        _uiState.update {
            it.copy(
                selectedChoice = choice,
                correctCount = it.correctCount + if (isCorrect) 1 else 0,
                answerStreak = newStreak,
                resultMessage = when {
                    !isCorrect -> "아쉬워요. 정답은 \"${question.answer}\"이에요."
                    newStreak >= COMBO_THRESHOLD -> "정답이에요! 🔥 ${newStreak}연속 정답!"
                    else -> "정답이에요!"
                },
            )
        }

        viewModelScope.launch {
            delay(1_000)
            advance()
        }
    }

    private fun advance() {
        val state = _uiState.value
        val nextIndex = state.questionIndex + 1
        if (nextIndex >= TOTAL_QUESTIONS) {
            val isRecord = previousBest?.let { state.correctCount > it } ?: (state.correctCount > 0)
            _uiState.update { it.copy(isComplete = true, isNewRecord = isRecord) }
            viewModelScope.launch {
                gameRepository.saveResult(GameType.ODD_WORD, state.correctCount)
                val statsBefore = statsBeforeGame
                if (statsBefore != null) {
                    val newlyUnlocked = findNewlyUnlockedAchievements(statsBefore, gameRepository.observePlayerStats().first())
                    if (newlyUnlocked.isNotEmpty()) {
                        _uiState.update { it.copy(newlyUnlockedAchievements = newlyUnlocked) }
                    }
                }
            }
        } else {
            isLocked = false
            _uiState.update {
                it.copy(
                    questionIndex = nextIndex,
                    question = OddWordGenerator.generate(),
                    selectedChoice = null,
                    resultMessage = "",
                )
            }
        }
    }

    companion object {
        const val TOTAL_QUESTIONS = 10
    }
}
