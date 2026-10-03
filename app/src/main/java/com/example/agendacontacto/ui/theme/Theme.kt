package com.example.agendacontacto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VioletaPrimarioOsc,
    onPrimary = SobrePrimarioOsc,
    primaryContainer = ContenedorPrimarioOsc,
    onPrimaryContainer = SobreContenedorPrimarioOsc,
    secondary = Color(0xFFD9B8FF),
    onSecondary = Color(0xFF3E2050),
    secondaryContainer = Color(0xFF563A68),
    onSecondaryContainer = Color(0xFFF3DAFF),
    tertiary = Color(0xFF80D9D2),
    background = FondoOscuro,
    onBackground = Color(0xFFE5E1E9),
    surface = SuperficieOscura,
    onSurface = Color(0xFFE5E1E9),
    surfaceVariant = SuperficieVarianteOsc,
    onSurfaceVariant = SobreVarianteOsc,
    outline = ContornoOsc,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = VioletaPrimario,
    onPrimary = SobrePrimario,
    primaryContainer = ContenedorPrimario,
    onPrimaryContainer = SobreContenedorPrimario,
    secondary = VioletaSecundario,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = ContenedorSecundario,
    onSecondaryContainer = Color(0xFF2E0A44),
    tertiary = TealAcento,
    background = FondoClaro,
    onBackground = Color(0xFF1B1B21),
    surface = SuperficieClara,
    onSurface = Color(0xFF1B1B21),
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = SobreVariante,
    outline = Contorno,
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun AgendaContactoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
