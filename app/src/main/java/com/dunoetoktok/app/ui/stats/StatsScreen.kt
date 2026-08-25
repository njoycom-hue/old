package com.dunoetoktok.app.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunoetoktok.app.model.GameResult
import com.dunoetoktok.app.model.formatScoreText
import com.dunoetoktok.app.ui.components.GameScreenScaffold
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val HISTORY_DATE_FORMATTER = DateTimeFormatter.ofPattern("M월 d일")

@Composable
fun StatsScreen(onBack: () -> Unit, viewModel: StatsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenScaffold(title = "나의 기록", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StreakCard(streakDays = uiState.streakDays)

            uiState.sections.forEach { section ->
                GameHistoryCard(section)
            }
        }
    }
}

@Composable
private fun StreakCard(streakDays: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            Text(
                if (streakDays > 0) "연속 ${streakDays}일째 훈련 중이에요!" else "오늘부터 두뇌 훈련을 시작해보세요!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GameHistoryCard(section: GameHistorySection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                "${section.gameType.emoji} ${section.gameType.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                section.bestScoreText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            if (section.recentResults.isEmpty()) {
                Text(
                    "아직 플레이 기록이 없어요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                section.recentResults.forEach { result ->
                    HistoryRow(result)
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(result: GameResult) {
    val date = Instant.ofEpochMilli(result.playedAt).atZone(ZoneId.systemDefault()).toLocalDate()
    Text(
        "${date.format(HISTORY_DATE_FORMATTER)} · ${result.gameType.formatScoreText(result.score)}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
