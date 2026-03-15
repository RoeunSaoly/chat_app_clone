package com.example.chat_app_clone.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MessengerDarkColorScheme = darkColorScheme(
    primary = MessengerBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF003166),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = MessengerPurple,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = DarkDivider,
    surfaceVariant = Color(0xFF3A3A3C),
)

private val MessengerLightColorScheme = lightColorScheme(
    primary = MessengerBlue,
    onPrimary = Color.White,
    primaryContainer = MessengerLightBlue,
    onPrimaryContainer = MessengerDarkBlue,
    secondary = MessengerPurple,
    onSecondary = Color.White,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Divider,
    surfaceVariant = Color(0xFFF2F2F7),
)

@Composable
fun Chat_app_cloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MessengerDarkColorScheme else MessengerLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}