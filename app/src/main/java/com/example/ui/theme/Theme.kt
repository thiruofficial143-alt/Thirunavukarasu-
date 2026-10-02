package com.example.ui.theme

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
    primary = BlueAccent,
    onPrimary = SlateTextPrimary,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = Color.White,
    secondary = AmberLight,
    onSecondary = SlateTextPrimary,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = AmberMuted,
    tertiary = EmeraldLight,
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark,
    surfaceVariant = SlateCardDark,
    onBackground = SlateTextDarkPrimary,
    onSurface = SlateTextDarkPrimary,
    onSurfaceVariant = SlateTextDarkSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = NavyDark,
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = AmberMuted,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = EmeraldTertiary,
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight,
    surfaceVariant = SlateCardLight,
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    onSurfaceVariant = SlateTextSecondary
)

@Composable
fun CampusCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our collegiate brand palette for strong identity
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
