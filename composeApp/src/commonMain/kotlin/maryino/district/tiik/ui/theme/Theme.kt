package maryino.district.tiik.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TiikLightColorScheme = lightColorScheme(
    primary = TiikColors.Ink,
    onPrimary = TiikColors.InkOnDark,
    primaryContainer = TiikColors.BgMuted,
    onPrimaryContainer = TiikColors.Ink,
    secondary = TiikColors.Ink3,
    onSecondary = TiikColors.InkOnDark,
    secondaryContainer = TiikColors.BgSubtle,
    onSecondaryContainer = TiikColors.Ink2,
    background = TiikColors.Bg,
    onBackground = TiikColors.Ink,
    surface = TiikColors.BgSurface,
    onSurface = TiikColors.Ink,
    surfaceVariant = TiikColors.BgMuted,
    onSurfaceVariant = TiikColors.Ink2,
    outline = TiikColors.Border,
    outlineVariant = TiikColors.Border2,
    error = TiikColors.Danger,
    onError = TiikColors.InkOnDark,
)

@Composable
fun TiikTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TiikLightColorScheme,
        typography = TiikTypography,
        content = content,
    )
}
