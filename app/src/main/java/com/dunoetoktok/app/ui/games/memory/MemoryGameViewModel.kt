package com.dunoetoktok.app.ui.games.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunoetoktok.app.data.repository.GameRepository
import com.dunoetoktok.app.model.GameType
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

private const val PREVIEW_SECONDS = 5

data class MemoryCardUi(
    val id: Int,
    val icon: String,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false,
)

data class MemoryGameUiState(
    val cards: List<MemoryCardUi> = emptyList(),
    val moves: Int = 0,
    val elapsedSeconds: Int = 0,
    val isComplete: Boolean = false,
    val isNewRecord: Boolean = false,
    val isPreviewing: Boolean = true,
    val previewSecondsRemaining: Int = PREVIEW_SECONDS,
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
    private var timerJob: Job? = null
    private var previewJob: Job? = null

    init {
        startNewGame()
    }

    fun startNewGame() {
        timerJob?.cancel()
        previewJob?.cancel()
        firstFlippedId = null
        isBoardLocked = true
        matchedPairCount = 0
        previousBest = null

        // All cards start face up so the player can memorize them before the preview ends.
        val cards = (ICONS + ICONS).shuffled()
            .mapIndexed { index, icon -> MemoryCardUi(id = index, icon = icon, isFaceUp = true) }
        _uiState.value = MemoryGameUiState(cards = cards)

        viewModelScope.launch {
            previousBest = gameRepository.observeBestScore(GameType.MEMORY).first()
        }

        previewJob = viewModelScope.launch {
            for (remaining in PREVIEW_SECONDS downTo 1) {
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
        if (state.isPreviewing) return
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
            if (matchedPairCount == ICONS.size) completeGame()
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

    private fun completeGame() {
        timerJob?.cancel()
        val moves = _uiState.value.moves
        val isRecord = previousBest?.let { moves < it } ?: true
        _uiState.update { it.copy(isComplete = true, isNewRecord = isRecord) }
        viewModelScope.launch { gameRepository.saveResult(GameType.MEMORY, moves) }
    }

    override fun onCleared() {
        timerJob?.cancel()
        previewJob?.cancel()
    }

    companion object {
        private val ICONS = listOf("🍎", "🍌", "🍇", "🍉", "🐶", "🐱")
    }
}
