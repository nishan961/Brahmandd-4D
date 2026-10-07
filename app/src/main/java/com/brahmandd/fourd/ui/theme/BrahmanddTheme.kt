package com.brahmandd.fourd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrahmanddColors = darkColorScheme(
    background = Color(0xFF030712),
    surface = Color(0xFF1E293B),
    primary = Color(0xFF38BDF8),
    secondary = Color(0xFF818CF8),
    tertiary = Color(0xFFA3E635),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0),
    onPrimary = Color(0xFF0F172A),
)

@Composable
fun BrahmanddTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrahmanddColors,
        content = content,
    )
}
