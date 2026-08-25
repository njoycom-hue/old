package com.dunoetoktok.app.ui.games.sequence

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class SequencePhase { IDLE, BUSY, WAITING_INPUT, GAME_OVER }

data class SequenceGameUiState(
    val phase: SequencePhase = SequencePhase.IDLE,
    val round: Int = 0,
    val litIndex: Int? = null,
    val message: String = "시작 버튼을 누르면 순서가 나와요",
    val bestRound: Int = 0,
    val isNewRecord: Boolean = false,
)

@HiltViewModel
class SequenceGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SequenceGameUiState())
    val uiState: StateFlow<SequenceGameUiState> = _uiState.asStateFlow()

    private val sequence = mutableListOf<Int>()
    private var userProgress = 0
    private var previousBest: Int? = null
    private var playbackJob: Job? = null

    init {
        viewModelScope.launch {
            gameRepository.observeBestScore(GameType.SEQUENCE).collect { best ->
                previousBest = best
                _uiState.update { it.copy(bestRound = best ?: 0) }
            }
        }
    }

    fun start() {
        sequence.clear()
        _uiState.update { it.copy(isNewRecord = false) }
        nextRound()
    }

    fun onColorClick(colorIndex: Int) {
        if (_uiState.value.phase != SequencePhase.WAITING_INPUT) return

        flashBriefly(colorIndex)

        if (colorIndex != sequence[userProgress]) {
            gameOver()
            return
        }

        userProgress++
        if (userProgress == sequence.size) {
            _uiState.update {
                it.copy(phase = SequencePhase.BUSY, message = "성공! 다음 라운드로 이어집니다")
            }
            viewModelScope.launch {
                delay(900)
                nextRound()
            }
        }
    }

    private fun nextRound() {
        sequence.add(Random.nextInt(4))
        userProgress = 0
        _uiState.update {
            it.copy(phase = SequencePhase.BUSY, round = sequence.size, message = "순서를 잘 보세요...")
        }
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            for (colorIndex in sequence) {
                _uiState.update { it.copy(litIndex = colorIndex) }
                delay(500)
                _uiState.update { it.copy(litIndex = null) }
                delay(200)
            }
            _uiState.update {
                it.copy(phase = SequencePhase.WAITING_INPUT, message = "이제 같은 순서로 눌러보세요")
            }
        }
    }

    private fun flashBriefly(colorIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(litIndex = colorIndex) }
            delay(250)
            _uiState.update { it.copy(litIndex = null) }
        }
    }

    private fun gameOver() {
        playbackJob?.cancel()
        val roundsCleared = sequence.size - 1
        val isRecord = previousBest?.let { roundsCleared > it } ?: (roundsCleared > 0)
        _uiState.update {
            it.copy(
                phase = SequencePhase.GAME_OVER,
                message = if (roundsCleared > 0) {
                    "아쉬워요! ${roundsCleared}라운드까지 성공했어요." + if (isRecord) " 새로운 최고 기록!" else ""
                } else {
                    "아쉬워요! 다시 도전해보세요."
                },
                isNewRecord = isRecord,
            )
        }
        if (roundsCleared > 0) {
            viewModelScope.launch { gameRepository.saveResult(GameType.SEQUENCE, roundsCleared) }
        }
    }

    override fun onCleared() {
        playbackJob?.cancel()
    }
}
