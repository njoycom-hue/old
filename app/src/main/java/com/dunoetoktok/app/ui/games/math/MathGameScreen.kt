package com.dunoetoktok.app.ui.games.math

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
import com.dunoetoktok.app.ui.components.AchievementUnlockBanner
import com.dunoetoktok.app.ui.components.CelebrationStamp
import com.dunoetoktok.app.ui.components.ChoiceButton
import com.dunoetoktok.app.ui.components.ChoiceState
import com.dunoetoktok.app.ui.components.GameScreenScaffold
import com.dunoetoktok.app.ui.components.PrimaryButton
import com.dunoetoktok.app.ui.components.StatusRow

@Composable
fun MathGameScreen(onBack: () -> Unit, viewModel: MathGameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenScaffold(title = "빠른 암산", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatusRow(
                "문제: ${minOf(uiState.questionIndex + 1, MathGameViewModel.TOTAL_QUESTIONS)} / ${MathGameViewModel.TOTAL_QUESTIONS}",
                "맞은 개수: ${uiState.correctCount}",
            )

            if (uiState.isComplete) {
                CelebrationStamp(visible = uiState.isNewRecord, modifier = Modifier.fillMaxWidth())
                Text(
                    "완료! ${uiState.correctCount} / ${MathGameViewModel.TOTAL_QUESTIONS}",
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
                AchievementUnlockBanner(uiState.newlyUnlockedAchievements)
                PrimaryButton(text = "다시 시작", onClick = { viewModel.startNewGame() })
            } else {
                val question = uiState.question
                Text(
                    question?.text.orEmpty(),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

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
                                text = choice.toString(),
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
