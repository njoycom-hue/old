package com.dunoetoktok.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.model.formatBestScoreText
import com.dunoetoktok.app.navigation.Routes
import com.dunoetoktok.app.ui.components.AppTopBar
import com.dunoetoktok.app.ui.components.HomeGameCard

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { AppTopBar(title = "두뇌톡톡", onBack = null) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "매일 조금씩, 즐겁게 두뇌를 움직여 보세요.\n치매 예방에 도움이 되는 두뇌 게임 4가지를 준비했어요.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            GameType.entries.chunked(2).forEach { rowGames ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowGames.forEach { gameType ->
                        HomeGameCard(
                            emoji = gameType.emoji,
                            title = gameType.title,
                            description = gameType.description,
                            bestScoreText = gameType.formatBestScoreText(uiState.bestScores[gameType]),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(gameType.route) },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HomeShortcutCard(
                    icon = Icons.Filled.BarChart,
                    label = "나의 기록",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Routes.STATS) },
                )
                HomeShortcutCard(
                    icon = Icons.Filled.Settings,
                    label = "설정",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Routes.SETTINGS) },
                )
            }
        }
    }
}

@Composable
private fun HomeShortcutCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
