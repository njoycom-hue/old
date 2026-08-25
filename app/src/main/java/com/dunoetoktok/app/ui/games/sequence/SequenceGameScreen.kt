package com.dunoetoktok.app.ui.games.sequence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunoetoktok.app.ui.components.GameScreenScaffold
import com.dunoetoktok.app.ui.components.PrimaryButton
import com.dunoetoktok.app.ui.components.StatusRow
import com.dunoetoktok.app.ui.theme.GameAccent

private val SEQUENCE_COLORS = listOf(GameAccent.Red, GameAccent.Blue, GameAccent.Yellow, GameAccent.Green)

@Composable
fun SequenceGameScreen(onBack: () -> Unit, viewModel: SequenceGameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenScaffold(title = "순서 기억하기", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatusRow("라운드: ${maxOf(uiState.round, 1)}", "최고 기록: ${uiState.bestRound}")

            Text(
                uiState.message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .aspectRatio(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(SEQUENCE_COLORS.size) { index ->
                    SequenceColorButton(
                        color = SEQUENCE_COLORS[index],
                        isLit = uiState.litIndex == index,
                        enabled = uiState.phase == SequencePhase.WAITING_INPUT,
                        onClick = { viewModel.onColorClick(index) },
                    )
                }
            }

            val buttonLabel = when (uiState.phase) {
                SequencePhase.IDLE -> "시작"
                SequencePhase.GAME_OVER -> "다시 시작"
                else -> "진행 중..."
            }
            PrimaryButton(text = buttonLabel, onClick = { viewModel.start() })
        }
    }
}

@Composable
private fun SequenceColorButton(color: Color, isLit: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .alpha(if (isLit) 1f else 0.45f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {}
}
