package com.dunoetoktok.app.ui.games.oddword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunoetoktok.app.ui.components.ChoiceButton
import com.dunoetoktok.app.ui.components.ChoiceState
import com.dunoetoktok.app.ui.components.GameScreenScaffold
import com.dunoetoktok.app.ui.components.PrimaryButton
import com.dunoetoktok.app.ui.components.StatusRow

@Composable
fun OddWordGameScreen(onBack: () -> Unit, viewModel: OddWordGameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenScaffold(title = "다른 것 찾기", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatusRow(
                "문제: ${minOf(uiState.questionIndex + 1, OddWordGameViewModel.TOTAL_QUESTIONS)} / ${OddWordGameViewModel.TOTAL_QUESTIONS}",
                "맞은 개수: ${uiState.correctCount}",
            )

            if (uiState.isComplete) {
                Text(
                    "완료! ${uiState.correctCount} / ${OddWordGameViewModel.TOTAL_QUESTIONS}",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    if (uiState.isNewRecord) "새로운 최고 기록이에요!" else "수고하셨어요!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                PrimaryButton(text = "다시 시작", onClick = { viewModel.startNewGame() })
            } else {
                Text(
                    "어울리지 않는 낱말은 무엇일까요?",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                val question = uiState.question
                question?.choices?.chunked(2)?.forEach { rowChoices ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        rowChoices.forEach { choice ->
                            val state = when {
                                uiState.selectedChoice == null -> ChoiceState.NONE
                                choice == question.answer -> ChoiceState.CORRECT
                                choice == uiState.selectedChoice -> ChoiceState.WRONG
                                else -> ChoiceState.NONE
                            }
                            ChoiceButton(
                                text = choice,
                                state = state,
                                enabled = uiState.selectedChoice == null,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.onChoiceSelected(choice) },
                            )
                        }
                    }
                }

                Text(
                    uiState.resultMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
