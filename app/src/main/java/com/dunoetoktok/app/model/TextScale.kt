package com.dunoetoktok.app.model

/** User-selectable UI text scale, aimed at older users who may need larger type. */
enum class TextScale(val multiplier: Float, val displayName: String) {
    NORMAL(1.0f, "보통"),
    LARGE(1.15f, "크게"),
    EXTRA_LARGE(1.3f, "아주 크게");

    companion object {
        fun fromName(name: String?): TextScale = entries.firstOrNull { it.name == name } ?: NORMAL
    }
}
