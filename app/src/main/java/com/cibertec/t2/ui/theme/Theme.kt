package com.cibertec.t2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MisFinanzasColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = RedExpense,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = LightText,
    onTertiary = LightText,
    onBackground = LightText,
    onSurface = LightText,
    error = RedExpense
)

@Composable
fun T2Theme(
    darkTheme: Boolean = true, 
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MisFinanzasColorScheme,
        typography = Typography,
        content = content
    )
}
