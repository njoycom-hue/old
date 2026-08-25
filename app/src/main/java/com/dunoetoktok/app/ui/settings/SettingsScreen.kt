package com.dunoetoktok.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dunoetoktok.app.model.TextScale
import com.dunoetoktok.app.ui.components.GameScreenScaffold

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenScaffold(title = "설정", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("글자 크기", style = MaterialTheme.typography.titleLarge)

            TextScale.entries.forEach { option ->
                TextScaleOptionRow(
                    option = option,
                    selected = option == uiState.textScale,
                    onClick = { viewModel.setTextScale(option) },
                )
            }
        }
    }
}

@Composable
private fun TextScaleOptionRow(option: TextScale, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 0.dp else 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(option.displayName, style = MaterialTheme.typography.titleMedium)
            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "선택됨",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
