package com.dunoetoktok.app.ui.games.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.Achievement
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.model.PlayerStats
import com.dunoetoktok.app.model.findNewlyUnlockedAchievements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** How many increasingly harder grids make up one full play-through. */
const val MEMORY_LEVEL_COUNT = 5

private const val LEVEL_TRANSITION_PAUSE_MS = 1_500L

/** One more pair per level, capped at [MEMORY_LEVEL_COUNT] levels (level 1 = 6 pairs, level 5 = 10 pairs). */
private fun pairsForLevel(level: Int): Int = 5 + level.coerceIn(1, MEMORY_LEVEL_COUNT)

/** Preview time shrinks slowly — every two levels — so it stays fair for older players. */
private fun previewSecondsForLevel(level: Int): Int = when {
    level <= 2 -> 5
    level <= 4 -> 4
    else -> 3
}

data class MemoryCardUi(
    val id: Int,
    val icon: String,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false,
)

data class MemoryGameUiState(
    val level: Int = 1,
    val cards: List<MemoryCardUi> = emptyList(),
    val moves: Int = 0,
    val elapsedSeconds: Int = 0,
    val isLevelComplete: Boolean = false,
    val isComplete: Boolean = false,
    val isNewRecord: Boolean = false,
    val isPreviewing: Boolean = true,
    val previewSecondsRemaining: Int = previewSecondsForLevel(1),
    val newlyUnlockedAchievements: List<Achievement> = emptyList(),
)

@HiltViewModel
class MemoryGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryGameUiState())
    val uiState: StateFlow<MemoryGameUiState> = _uiState.asStateFlow()

    private var firstFlippedId: Int? = null
    private var isBoardLocked = false
    private var matchedPairCount = 0
    private var previousBest: Int? = null
    private var statsBeforeGame: PlayerStats? = null
    private var timerJob: Job? = null
    private var previewJob: Job? = null

    init {
        startNewGame()
    }

    fun startNewGame() {
        previousBest = null
        statsBeforeGame = null
        _uiState.value = MemoryGameUiState()

        viewModelScope.launch {
            previousBest = gameRepository.observeBestScore(GameType.MEMORY).first()
            statsBeforeGame = gameRepository.observePlayerStats().first()
        }

        startLevel(level = 1)
    }

    private fun startLevel(level: Int) {
        timerJob?.cancel()
        previewJob?.cancel()
        firstFlippedId = null
        isBoardLocked = true
        matchedPairCount = 0

        val icons = ICONS.take(pairsForLevel(level))
        // All cards start face up so the player can memorize them before the preview ends.
        val cards = (icons + icons).shuffled()
            .mapIndexed { index, icon -> MemoryCardUi(id = index, icon = icon, isFaceUp = true) }
        val previewSeconds = previewSecondsForLevel(level)

        _uiState.update {
            it.copy(
                level = level,
                cards = cards,
                moves = 0,
                isLevelComplete = false,
                isPreviewing = true,
                previewSecondsRemaining = previewSeconds,
            )
        }

        previewJob = viewModelScope.launch {
            for (remaining in previewSeconds downTo 1) {
                _uiState.update { it.copy(previewSecondsRemaining = remaining) }
                delay(1_000)
            }
            _uiState.update { state ->
                state.copy(
                    isPreviewing = false,
                    cards = state.cards.map { it.copy(isFaceUp = false) },
                )
            }
            isBoardLocked = false
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun onCardClick(cardId: Int) {
        if (isBoardLocked) return
        val state = _uiState.value
        if (state.isPreviewing || state.isLevelComplete || state.isComplete) return
        val clicked = state.cards.firstOrNull { it.id == cardId } ?: return
        if (clicked.isFaceUp || clicked.isMatched) return

        setFaceUp(cardId, true)

        val firstId = firstFlippedId
        if (firstId == null) {
            firstFlippedId = cardId
            return
        }

        isBoardLocked = true
        _uiState.update { it.copy(moves = it.moves + 1) }

        val firstIcon = state.cards.first { it.id == firstId }.icon
        if (firstIcon == clicked.icon) {
            setMatched(firstId, cardId)
            matchedPairCount++
            firstFlippedId = null
            isBoardLocked = false
            if (matchedPairCount == pairsForLevel(state.level)) {
                onLevelCleared(state.level)
            }
        } else {
            viewModelScope.launch {
                delay(700)
                setFaceUp(firstId, false)
                setFaceUp(cardId, false)
                firstFlippedId = null
                isBoardLocked = false
            }
        }
    }

    private fun onLevelCleared(level: Int) {
        timerJob?.cancel()
        if (level >= MEMORY_LEVEL_COUNT) {
            completeSession(level)
        } else {
            _uiState.update { it.copy(isLevelComplete = true) }
            viewModelScope.launch {
                delay(LEVEL_TRANSITION_PAUSE_MS)
                startLevel(level + 1)
            }
        }
    }

    private fun setFaceUp(cardId: Int, faceUp: Boolean) {
        _uiState.update { state ->
            state.copy(cards = state.cards.map { if (it.id == cardId) it.copy(isFaceUp = faceUp) else it })
        }
    }

    private fun setMatched(firstId: Int, secondId: Int) {
        _uiState.update { state ->
            state.copy(
                cards = state.cards.map {
                    if (it.id == firstId || it.id == secondId) it.copy(isMatched = true) else it
                },
            )
        }
    }

    private fun completeSession(finalLevel: Int) {
        val isRecord = previousBest?.let { finalLevel > it } ?: true
        _uiState.update { it.copy(isComplete = true, isNewRecord = isRecord) }
        viewModelScope.launch {
            gameRepository.saveResult(GameType.MEMORY, finalLevel)
            val statsBefore = statsBeforeGame
            if (statsBefore != null) {
                val newlyUnlocked = findNewlyUnlockedAchievements(statsBefore, gameRepository.observePlayerStats().first())
                if (newlyUnlocked.isNotEmpty()) {
                    _uiState.update { it.copy(newlyUnlockedAchievements = newlyUnlocked) }
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        previewJob?.cancel()
    }

    companion object {
        private val ICONS = listOf("🍎", "🍌", "🍇", "🍉", "🐶", "🐱", "🐻", "🐰", "🍊", "🍓")
    }
}
