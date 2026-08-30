package com.dunoetoktok.app.model

import com.dunoetoktok.app.util.ExperienceCalculator

data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val isUnlocked: (PlayerStats) -> Boolean,
)

/** Fixed, hand-picked milestones — not a formula — so each one feels like a deliberate reward. */
val ACHIEVEMENTS: List<Achievement> = listOf(
    Achievement(
        id = "first_game",
        emoji = "🌱",
        title = "첫 걸음",
        description = "게임을 처음으로 완료했어요",
        isUnlocked = { it.totalGamesPlayed >= 1 },
    ),
    Achievement(
        id = "ten_games",
        emoji = "🔥",
        title = "꾸준한 연습",
        description = "게임을 10번 완료했어요",
        isUnlocked = { it.totalGamesPlayed >= 10 },
    ),
    Achievement(
        id = "fifty_games",
        emoji = "🏆",
        title = "두뇌 마스터",
        description = "게임을 50번 완료했어요",
        isUnlocked = { it.totalGamesPlayed >= 50 },
    ),
    Achievement(
        id = "streak_3",
        emoji = "📅",
        title = "3일 연속",
        description = "3일 연속으로 훈련했어요",
        isUnlocked = { it.longestStreak >= 3 },
    ),
    Achievement(
        id = "streak_7",
        emoji = "⭐",
        title = "일주일 연속",
        description = "7일 연속으로 훈련했어요",
        isUnlocked = { it.longestStreak >= 7 },
    ),
    Achievement(
        id = "streak_30",
        emoji = "👑",
        title = "한 달 챔피언",
        description = "30일 연속으로 훈련했어요",
        isUnlocked = { it.longestStreak >= 30 },
    ),
    Achievement(
        id = "memory_ace",
        emoji = "🃏",
        title = "기억력 에이스",
        description = "기억력 카드 짝맞추기의 모든 레벨을 클리어했어요",
        isUnlocked = { (it.bestScores[GameType.MEMORY] ?: 0) >= 5 },
    ),
    Achievement(
        id = "sequence_master",
        emoji = "🔵",
        title = "순서의 달인",
        description = "순서 기억하기에서 10라운드를 넘겼어요",
        isUnlocked = { (it.bestScores[GameType.SEQUENCE] ?: 0) >= 10 },
    ),
    Achievement(
        id = "math_perfect",
        emoji = "➕",
        title = "암산 만점",
        description = "빠른 암산에서 만점을 받았어요",
        isUnlocked = { (it.bestScores[GameType.MATH] ?: 0) >= 10 },
    ),
    Achievement(
        id = "word_perfect",
        emoji = "🍎",
        title = "낱말 박사",
        description = "다른 것 찾기에서 만점을 받았어요",
        isUnlocked = { (it.bestScores[GameType.ODD_WORD] ?: 0) >= 10 },
    ),
    Achievement(
        id = "level_5",
        emoji = "💪",
        title = "레벨 5 달성",
        description = "꾸준히 훈련해서 레벨 5에 도달했어요",
        isUnlocked = { ExperienceCalculator.levelForTotalXp(it.totalXp).level >= 5 },
    ),
)

/** Achievements that flip from locked to unlocked between two stats snapshots — the celebration moment. */
fun findNewlyUnlockedAchievements(before: PlayerStats, after: PlayerStats): List<Achievement> =
    ACHIEVEMENTS.filter { !it.isUnlocked(before) && it.isUnlocked(after) }
