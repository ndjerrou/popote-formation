package fr.formation.popote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// region theme
// Palette inspirée de Kotlin : violet principal, accents magenta/orange
private val KotlinPurple = Color(0xFF7F52FF)
private val KotlinMagenta = Color(0xFFC811E2)
private val KotlinOrange = Color(0xFFE54857)

private val LightColors = lightColorScheme(
    primary = KotlinPurple,
    secondary = KotlinMagenta,
    tertiary = KotlinOrange,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB9A3FF),
    secondary = Color(0xFFE58CF2),
    tertiary = Color(0xFFF29AA3),
)

@Composable
fun PopoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
// endregion theme
