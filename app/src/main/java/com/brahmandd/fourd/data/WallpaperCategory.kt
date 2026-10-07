package com.brahmandd.fourd.data

enum class WallpaperCategory(
    val key: String,
    val label: String,
    val description: String,
    val accentStart: Long,
    val accentEnd: Long,
) {
    DEEP_SPACE(
        key = "deep_space",
        label = "Deep Space Nebula",
        description = "Nebula drift, star density, and cyan-to-indigo depth fields",
        accentStart = 0xFF38BDF8,
        accentEnd = 0xFF818CF8,
    ),
    CYBER_GRID(
        key = "cyber_grid",
        label = "Cybernetic Grid",
        description = "Electric matrix geometry with precise tilt-driven motion",
        accentStart = 0xFFA3E635,
        accentEnd = 0xFF38BDF8,
    ),
    AMOLED_QUANTUM(
        key = "amoled_quantum",
        label = "Amoled Quantum",
        description = "True black OLED depth with minimal floating orb systems",
        accentStart = 0xFF3B82F6,
        accentEnd = 0xFF8B5CF6,
    ),
    SHIVA(
        key = "shiva",
        label = "Shiva • Kailash",
        description = "A meditating Shiva beneath Mount Kailash, with layered gyro parallax",
        accentStart = 0xFF38BDF8,
        accentEnd = 0xFF8B5CF6,
    );

    companion object {
        fun fromKey(value: String?): WallpaperCategory =
            entries.firstOrNull { it.key == value } ?: DEEP_SPACE
    }
}
