package com.heavystudio.helpabroad.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GeminiLightBlue,
    onPrimary = Color(0xFF00325B),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = GeminiBlue,
    onPrimary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

/**
 * A custom Material 3 theme for the Help Abroad application.
 *
 * This composable function applies the application's color scheme, typography, and shapes.
 * It supports both light and dark themes, and can optionally use dynamic colors on Android 12+
 * to match the user's system wallpaper.
 *
 * @param darkTheme Whether to use the dark color scheme. Defaults to the system's setting.
 * @param dynamicColor Whether to use dynamic colors generated from the user's wallpaper.
 *                     This is only available on Android 12 (API 31) and above. If `true` on an
 *                     older device, it will fall back to the default `LightColorScheme` or `DarkColorScheme`.
 *                     Defaults to `false`.
 * @param content The composable content to which this theme will be applied.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the theme.
 */
@Composable
fun HelpAbroadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}