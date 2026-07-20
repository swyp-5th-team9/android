package org.app.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/**
 * 시스템 글꼴 크기(fontScale) 상한.
 * 사용자가 기기 글꼴을 크게 설정해도 앱 UI가 시안 대비 과도하게 커지지 않도록 제한한다.
 */
private const val MAX_FONT_SCALE = 1.3f

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
    val current = LocalDensity.current
    val cappedDensity = Density(
        density = current.density,
        fontScale = current.fontScale.coerceAtMost(MAX_FONT_SCALE),
    )
    CompositionLocalProvider(LocalDensity provides cappedDensity) {
        ProvideMoballColorsAndTypography(
            colors = defaultMoballColors,
            typography = defaultMoballTypography,
        ) {
            MaterialTheme(
                content = content,
            )
        }
    }
}
