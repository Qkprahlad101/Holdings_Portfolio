package com.example.holdings_portfolio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PortfolioBlueLight,
    onPrimary = Color.White,
    surface = SurfaceBgLight,
    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF8F98A9),
    error = PortfolioRedLight,
    onError = Color.White,
    tertiary = Pink40

)

private val DarkColorScheme = darkColorScheme(
    primary = PortfolioBlueDark,
    onPrimary = Color.White,
    surface = SurfaceBgDark,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB0BEC5),
    error = PortfolioRedDark,
    onError = Color.Black,
    tertiary = Pink80
)

@Composable
fun HoldingsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
