package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = SacredSaffron,
    onPrimary = Color.White,
    primaryContainer = SaffronSoftContainer,
    onPrimaryContainer = OnSaffronContainer,
    secondary = KrishnaIndigo,
    onSecondary = Color.White,
    secondaryContainer = IndigoSoftContainer,
    onSecondaryContainer = OnIndigoContainer,
    tertiary = GoldLeaf,
    onTertiary = Color.White,
    tertiaryContainer = GoldLeafSoft,
    onTertiaryContainer = SepiaInk,
    background = ParchmentCream,
    onBackground = SepiaInk,
    surface = ParchmentCream,
    onSurface = SepiaInk,
    surfaceVariant = ParchmentElevated,
    onSurfaceVariant = SepiaMuted,
    outline = ParchmentBorder,
    error = CrimsonError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SaffronGoldDark,
    onPrimary = Color(0xFF472000),
    primaryContainer = Color(0xFF6B3200),
    onPrimaryContainer = SaffronSoftContainer,
    secondary = Color(0xFFB4C5FF),
    onSecondary = KrishnaIndigo,
    secondaryContainer = KrishnaIndigoLight,
    onSecondaryContainer = IndigoSoftContainer,
    tertiary = GoldLeafSoft,
    onTertiary = Color(0xFF3E2D00),
    background = CosmicNight,
    onBackground = IvoryText,
    surface = CosmicSurface,
    onSurface = IvoryText,
    surfaceVariant = CosmicCard,
    onSurfaceVariant = IvoryMuted,
    outline = Color(0xFF3D4B72),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

private val GeetaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun GeetaScrollTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GeetaTypography,
        shapes = GeetaShapes,
        content = content
    )
}
