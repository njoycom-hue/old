package com.dunoetoktok.app.model

import com.dunoetoktok.app.navigation.Routes

/**
 * The four brain-training games. [storageKey] is the stable string persisted in Room —
 * never change existing values, only append new games.
 */
enum class GameType(
    val storageKey: String,
    val route: String,
    val emoji: String,
    val title: String,
    val description: String,
    val lowerScoreIsBetter: Boolean,
) {
    MEMORY(
        storageKey = "memory",
        route = Routes.MEMORY_GAME,
        emoji = "🃏",
        title = "기억력 카드 짝맞추기",
        description = "같은 그림 카드를 찾아보세요",
        lowerScoreIsBetter = false,
    ),
    SEQUENCE(
        storageKey = "sequence",
        route = Routes.SEQUENCE_GAME,
        emoji = "🔵",
        title = "순서 기억하기",
        description = "불이 켜진 순서를 똑같이 눌러보세요",
        lowerScoreIsBetter = false,
    ),
    MATH(
        storageKey = "math",
        route = Routes.MATH_GAME,
        emoji = "➕",
        title = "빠른 암산",
        description = "간단한 계산 문제를 풀어보세요",
        lowerScoreIsBetter = false,
    ),
    ODD_WORD(
        storageKey = "odd_word",
        route = Routes.ODD_WORD_GAME,
        emoji = "🍎",
        title = "다른 것 찾기",
        description = "어울리지 않는 낱말을 찾아보세요",
        lowerScoreIsBetter = false,
    ),
    ;

    companion object {
        fun fromStorageKey(key: String): GameType? = entries.firstOrNull { it.storageKey == key }
    }
}

/** Renders a raw score the way this game's unit is meant to read, e.g. "레벨 3까지 완료", "3라운드", "8 / 10". */
fun GameType.formatScoreText(score: Int): String = when (this) {
    GameType.MEMORY -> "레벨 ${score}까지 완료"
    GameType.SEQUENCE -> "${score}라운드"
    GameType.MATH, GameType.ODD_WORD -> "$score / 10"
}

fun GameType.formatBestScoreText(score: Int?): String =
    if (score == null) "최고 기록: -" else "최고 기록: ${formatScoreText(score)}"
