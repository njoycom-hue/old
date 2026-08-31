package com.dunoetoktok.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dunoetoktok.app.model.Achievement
import com.dunoetoktok.app.model.GameType
import com.dunoetoktok.app.ui.theme.GameAccent
import com.dunoetoktok.app.ui.theme.TextPrimary
import com.dunoetoktok.app.ui.theme.darken
import com.dunoetoktok.app.ui.theme.gradientBrush

enum class ChoiceState { NONE, CORRECT, WRONG }

/** Soft drop shadow so light text stays legible over any spot on a busy gradient background. */
private val legibilityShadow = Shadow(color = Color.Black.copy(alpha = 0.25f), offset = Offset(0f, 2f), blurRadius = 5f)

/** Standard scaffold for every non-home screen: orange top bar with a back button. */
@Composable
fun GameScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    Scaffold(topBar = { AppTopBar(title = title, onBack = onBack) }, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)?) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.ExtraBold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "홈으로")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

/**
 * A "chunky" 3D-press button: a darker base layer peeks out beneath the button, and pressing it
 * slides the top layer down to meet it — the tactile, toy-like button style common in casual games,
 * as opposed to a flat Material button.
 */
@Composable
private fun ChunkyButton(
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 60.dp,
    depth: Dp = 6.dp,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) depth else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chunky-press-offset",
    )

    Box(modifier = modifier.height(height + depth)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.BottomCenter)
                .clip(shape)
                .background(if (enabled) containerColor.darken() else containerColor.darken().copy(alpha = 0.5f)),
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.TopCenter)
                .offset(y = pressOffset),
            shape = shape,
            contentPadding = PaddingValues(horizontal = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor.copy(alpha = 0.6f),
                disabledContentColor = contentColor.copy(alpha = 0.8f),
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            content = content,
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ChunkyButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun ChoiceButton(
    text: String,
    state: ChoiceState,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val containerColor = when (state) {
        ChoiceState.NONE -> MaterialTheme.colorScheme.surface
        ChoiceState.CORRECT -> GameAccent.Green
        ChoiceState.WRONG -> GameAccent.Red
    }
    val contentColor = if (state == ChoiceState.NONE) MaterialTheme.colorScheme.onSurface else Color.White

    ChunkyButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        height = 64.dp,
    ) {
        Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun HomeGameCard(
    gameType: GameType,
    bestScoreText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.25f))
            .clip(shape)
            .background(gameType.gradientBrush())
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(gameType.emoji, fontSize = 26.sp)
            }
            Text(
                gameType.title,
                style = MaterialTheme.typography.titleMedium.copy(shadow = legibilityShadow),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Text(
                gameType.description,
                style = MaterialTheme.typography.bodyMedium.copy(shadow = legibilityShadow),
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
            )
            Text(
                bestScoreText,
                style = MaterialTheme.typography.bodyMedium.copy(shadow = legibilityShadow),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
        }
    }
}

@Composable
fun AchievementUnlockBanner(achievements: List<Achievement>, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = achievements.isNotEmpty(),
        enter = scaleIn(
            initialScale = 0.6f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        ) + fadeIn(),
        modifier = modifier,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = GameAccent.Yellow,
                contentColor = TextPrimary,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("🎉 새 업적 달성! 🎉", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                achievements.forEach { achievement ->
                    Text(
                        "${achievement.emoji} ${achievement.title}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/**
 * A big emoji that pops in with a bouncy overshoot — dropped in next to a "new record" or
 * "level complete" message so the moment feels like a reward instead of a status update.
 */
@Composable
fun CelebrationStamp(visible: Boolean, modifier: Modifier = Modifier, emoji: String = "🎉") {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        ) + fadeIn(),
        modifier = modifier,
    ) {
        Text(emoji, fontSize = 56.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun StatusRow(vararg items: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items.forEach { item ->
            Text(
                item,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
