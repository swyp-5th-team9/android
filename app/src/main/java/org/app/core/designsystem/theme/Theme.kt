package org.app.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object MoballTheme {
    val colors: MoballColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMoballColors.current

    val typography: MoballTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalMoballTypography.current
}

@Composable
fun ProvideMoballColorsAndTypography(
    colors: MoballColors,
    typography: MoballTypography,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMoballColors provides colors,
        LocalMoballTypography provides typography,
        content = content,
    )
}

@Composable
fun MoballTheme(content: @Composable () -> Unit) {
    ProvideMoballColorsAndTypography(
        colors = defaultMoballColors,
        typography = defaultMoballTypography,
    ) {
        MaterialTheme(
            content = content,
        )
    }
}
