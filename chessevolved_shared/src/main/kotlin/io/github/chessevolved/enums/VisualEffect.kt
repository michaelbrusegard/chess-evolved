package io.github.chessevolved.enums

enum class VisualEffectType(
    val value: Float,
) {
    SHIELD_ACTIVE(0.2f),
    SHIELD_BREAK(0.075f),
    EXPLOSION(0.1f),
}

enum class VisualEffectSize(
    val value: Int,
) {
    NORMAL(1), // 1x1
    MEDIUM(3), // 3x3
    LARGE(5), // 5x5
}
