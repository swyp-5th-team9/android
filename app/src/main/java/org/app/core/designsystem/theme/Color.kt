package org.app.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val white = Color(0xFFFFFFFF)
val black = Color(0xFF000000)

val coolNeutral130 = Color(0xFFF1F2F4)
val coolNeutral150 = Color(0xFFEBEDF0)
val coolNeutral200 = Color(0xFFDCE0E5)
val coolNeutral300 = Color(0xFFBFC6CF)
val coolNeutral500 = Color(0xFF8591A3)
val coolNeutral650 = Color(0xFF5E687E)
val coolNeutral800 = Color(0xFF3D4652)

val lime500 = Color(0xFFC8E263)

// Accent Palette
val lime300 = Color(0xFFE8F5A3)
val lime100 = Color(0xFFFAFDE2)

// State Palette
val green800 = Color(0xFF269130)
val green100 = Color(0xFFE7F2E8)
val green200 = Color(0xFFB7DABA)
val red950 = Color(0xFFE53234)
val red100 = Color(0xFFFDEEEE)
val red400 = Color(0xFFF9B2B2)

@Immutable
data class MoballColors(
    // Background
    val backgroundBase: Color,
    val backgroundSurface: Color,
    val backgroundScrim: Color,
    // Border & Line
    val borderNormal: Color,
    val borderStrong: Color,
    val borderActive: Color,
    val borderDisabled: Color,
    val borderStrongInverse: Color,
    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textQuaternary: Color,
    val textDisabled: Color,
    val textPrimaryInverse: Color,
    // Icon
    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconTertiary: Color,
    val iconQuaternary: Color,
    val iconDisabled: Color,
    val iconPrimaryInverse: Color,
    // Static
    val staticWhite: Color,
    val staticBlack: Color,
    // Accent
    val accentPrimary: Color,
    val accentSecondaryLight: Color,
    val accentTertiaryLight: Color,
    val accentDisabled: Color,
    // State
    val statePositive: Color,
    val statePositiveLight: Color,
    val statePositiveDisabled: Color,
    val stateNegative: Color,
    val stateNegativeLight: Color,
    val stateNegativeDisabled: Color,
)

val defaultMoballColors = MoballColors(
    // Background
    backgroundBase = white,
    backgroundSurface = coolNeutral130,
    backgroundScrim = coolNeutral800,
    // Border & Line
    borderNormal = coolNeutral150,
    borderStrong = coolNeutral200,
    borderActive = lime500,
    borderDisabled = coolNeutral130,
    borderStrongInverse = white,
    // Text
    textPrimary = coolNeutral800,
    textSecondary = coolNeutral650,
    textTertiary = coolNeutral500,
    textQuaternary = coolNeutral300,
    textDisabled = coolNeutral200,
    textPrimaryInverse = white,
    // Icon
    iconPrimary = coolNeutral800,
    iconSecondary = coolNeutral650,
    iconTertiary = coolNeutral500,
    iconQuaternary = coolNeutral300,
    iconDisabled = coolNeutral200,
    iconPrimaryInverse = white,
    // Static
    staticWhite = white,
    staticBlack = black,
    // Accent
    accentPrimary = lime500,
    accentSecondaryLight = lime300,
    accentTertiaryLight = lime100,
    accentDisabled = coolNeutral130,
    // State
    statePositive = green800,
    statePositiveLight = green100,
    statePositiveDisabled = green200,
    stateNegative = red950,
    stateNegativeLight = red100,
    stateNegativeDisabled = red400,
)

val LocalMoballColors = staticCompositionLocalOf { defaultMoballColors }

@Preview(showBackground = true)
@Composable
private fun MoballColorsPreview() {
    MoballTheme {
        Column {
            listOf(
                white,
                black,
                coolNeutral130,
                coolNeutral150,
                coolNeutral200,
                coolNeutral300,
                coolNeutral500,
                coolNeutral650,
                coolNeutral800,
                lime500,
                lime300,
                lime100,
                green800,
                green100,
                green200,
                red950,
                red100,
                red400,
            ).chunked(6).forEach { rowColors ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    rowColors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(40.dp)
                                .background(c),
                        )
                    }
                }
            }
        }
    }
}
