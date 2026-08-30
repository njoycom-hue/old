package com.dunoetoktok.app.ui.games.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunoetoktok.app.ui.components.AchievementUnlockBanner
import com.dunoetoktok.app.ui.components.GameScreenScaffold
import com.dunoetoktok.app.ui.components.PrimaryButton
import com.dunoetoktok.app.ui.components.StatusRow

@Composable
fun MemoryGameScreen(onBack: () -> Unit, viewModel: MemoryGameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenScaffold(title = "기억력 카드 짝맞추기", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatusRow("레벨 ${uiState.level} / $MEMORY_LEVEL_COUNT", "시도 횟수: ${uiState.moves}")

            when {
                uiState.isPreviewing -> Text(
                    "카드를 잘 기억하세요! ${uiState.previewSecondsRemaining}초 후 시작합니다",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                uiState.isLevelComplete -> Text(
                    "레벨 ${uiState.level} 완료! 다음 레벨로 이어집니다 🎉",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.cards, key = { it.id }) { card ->
                    MemoryCard(
                        icon = card.icon,
                        isRevealed = card.isFaceUp || card.isMatched,
                        isClickable = !uiState.isPreviewing && !card.isMatched,
                        onClick = { viewModel.onCardClick(card.id) },
                    )
                }
            }

            if (uiState.isComplete) {
                Text(
                    if (uiState.isNewRecord) {
                        "축하합니다! 모든 레벨을 완료했어요! 새로운 최고 기록!"
                    } else {
                        "수고하셨어요! 모든 레벨을 완료했어요!"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                AchievementUnlockBanner(uiState.newlyUnlockedAchievements)
            }

            PrimaryButton(text = "다시 시작", onClick = { viewModel.startNewGame() })
        }
    }
}

@Composable
private fun MemoryCard(icon: String, isRevealed: Boolean, isClickable: Boolean, onClick: () -> Unit) {
    val background = if (isRevealed) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(enabled = isClickable, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isRevealed) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
