package org.app.core.designsystem.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.moball.app.R.font.pretendard_bold
import com.moball.app.R.font.pretendard_extrabold
import com.moball.app.R.font.pretendard_medium
import com.moball.app.R.font.pretendard_regular
import com.moball.app.R.font.pretendard_semibold
import org.app.core.designsystem.theme.MoballTheme.typography

object PretendardFont {
    val Regular = FontFamily(Font(pretendard_regular))
    val Medium = FontFamily(Font(pretendard_medium))
    val SemiBold = FontFamily(Font(pretendard_semibold))
    val Bold = FontFamily(Font(pretendard_bold))
    val ExtraBold = FontFamily(Font(pretendard_extrabold))
}

sealed interface TypographyTokens {
    @Immutable
    data class Heading1(
        val extrabold26: TextStyle,
        val bold26: TextStyle,
        val semibold26: TextStyle,
        val extrabold24: TextStyle,
        val bold24: TextStyle,
        val semibold24: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Heading2(
        val extrabold22: TextStyle,
        val bold22: TextStyle,
        val semibold22: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Heading3(
        val extrabold20: TextStyle,
        val bold20: TextStyle,
        val semibold20: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Heading5(
        val extrabold18: TextStyle,
        val bold18: TextStyle,
        val semibold18: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Heading6(
        val extrabold16: TextStyle,
        val bold16: TextStyle,
        val semibold16: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Heading7(
        val extrabold14: TextStyle,
        val bold14: TextStyle,
        val semibold14: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Body(
        val semibold14: TextStyle,
        val medium14: TextStyle,
        val medium16: TextStyle,
        val regular14: TextStyle,
    ) : TypographyTokens

    @Immutable
    data class Caption(
        val medium12: TextStyle,
        val regular12: TextStyle,
    ) : TypographyTokens
}

@Immutable
data class MoballTypography(
    val heading1: TypographyTokens.Heading1,
    val heading2: TypographyTokens.Heading2,
    val heading3: TypographyTokens.Heading3,
    val heading5: TypographyTokens.Heading5,
    val heading6: TypographyTokens.Heading6,
    val heading7: TypographyTokens.Heading7,
    val body: TypographyTokens.Body,
    val caption: TypographyTokens.Caption,
)

private fun moballTextStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = (-0.01).em,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
        ),
    )

val defaultMoballTypography = MoballTypography(
    heading1 = TypographyTokens.Heading1(
        extrabold26 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 26.sp,
            lineHeight = 1.25.em,
        ),
        bold26 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 26.sp,
            lineHeight = 1.25.em,
        ),
        semibold26 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 26.sp,
            lineHeight = 1.25.em,
        ),
        extrabold24 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 24.sp,
            lineHeight = 1.25.em,
        ),
        bold24 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 24.sp,
            lineHeight = 1.25.em,
        ),
        semibold24 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 24.sp,
            lineHeight = 1.25.em,
        ),
    ),
    heading2 = TypographyTokens.Heading2(
        extrabold22 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 22.sp,
            lineHeight = 1.3.em,
        ),
        bold22 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 22.sp,
            lineHeight = 1.3.em,
        ),
        semibold22 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 22.sp,
            lineHeight = 1.3.em,
        ),
    ),
    heading3 = TypographyTokens.Heading3(
        extrabold20 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 20.sp,
            lineHeight = 1.3.em,
        ),
        bold20 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 20.sp,
            lineHeight = 1.3.em,
        ),
        semibold20 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 20.sp,
            lineHeight = 1.3.em,
        ),
    ),
    heading5 = TypographyTokens.Heading5(
        extrabold18 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 18.sp,
            lineHeight = 1.4.em,
        ),
        bold18 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 18.sp,
            lineHeight = 1.4.em,
        ),
        semibold18 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 18.sp,
            lineHeight = 1.4.em,
        ),
    ),
    heading6 = TypographyTokens.Heading6(
        extrabold16 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 16.sp,
            lineHeight = 1.4.em,
        ),
        bold16 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 16.sp,
            lineHeight = 1.4.em,
        ),
        semibold16 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 16.sp,
            lineHeight = 1.4.em,
        ),
    ),
    heading7 = TypographyTokens.Heading7(
        extrabold14 = moballTextStyle(
            fontFamily = PretendardFont.ExtraBold,
            fontSize = 14.sp,
            lineHeight = 1.4.em,
        ),
        bold14 = moballTextStyle(
            fontFamily = PretendardFont.Bold,
            fontSize = 14.sp,
            lineHeight = 1.4.em,
        ),
        semibold14 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 14.sp,
            lineHeight = 1.4.em,
        ),
    ),
    body = TypographyTokens.Body(
        semibold14 = moballTextStyle(
            fontFamily = PretendardFont.SemiBold,
            fontSize = 14.sp,
            lineHeight = 1.5.em,
        ),
        medium14 = moballTextStyle(
            fontFamily = PretendardFont.Medium,
            fontSize = 14.sp,
            lineHeight = 1.5.em,
        ),
        medium16 = moballTextStyle(
            fontFamily = PretendardFont.Medium,
            fontSize = 16.sp,
            lineHeight = 1.5.em,
        ),
        regular14 = moballTextStyle(
            fontFamily = PretendardFont.Regular,
            fontSize = 14.sp,
            lineHeight = 1.5.em,
        ),
    ),
    caption = TypographyTokens.Caption(
        medium12 = moballTextStyle(
            fontFamily = PretendardFont.Medium,
            fontSize = 12.sp,
            lineHeight = 1.35.em,
        ),
        regular12 = moballTextStyle(
            fontFamily = PretendardFont.Regular,
            fontSize = 12.sp,
            lineHeight = 1.35.em,
        ),
    ),
)

val LocalMoballTypography = staticCompositionLocalOf { defaultMoballTypography }

@Preview(showBackground = true)
@Composable
fun MoballTypographyPreview() {
    MoballTheme {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Text("Heading1.extrabold26", style = typography.heading1.extrabold26)
            Text("Heading1.bold26", style = typography.heading1.bold26)
            Text("Heading1.semibold26", style = typography.heading1.semibold26)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Heading1.extrabold24", style = typography.heading1.extrabold24)
            Text("Heading1.bold24", style = typography.heading1.bold24)
            Text("Heading1.semibold24", style = typography.heading1.semibold24)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Heading2.extrabold22", style = typography.heading2.extrabold22)
            Text("Heading2.bold22", style = typography.heading2.bold22)
            Text("Heading2.semibold22", style = typography.heading2.semibold22)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Heading3.extrabold20", style = typography.heading3.extrabold20)
            Text("Heading3.bold20", style = typography.heading3.bold20)
            Text("Heading3.semibold20", style = typography.heading3.semibold20)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Heading5.extrabold18", style = typography.heading5.extrabold18)
            Text("Heading5.bold18", style = typography.heading5.bold18)
            Text("Heading5.semibold18", style = typography.heading5.semibold18)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Heading6.extrabold16", style = typography.heading6.extrabold16)
            Text("Heading6.bold16", style = typography.heading6.bold16)
            Text("Heading6.semibold16", style = typography.heading6.semibold16)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Heading7.extrabold14", style = typography.heading7.extrabold14)
            Text("Heading7.bold14", style = typography.heading7.bold14)
            Text("Heading7.semibold14", style = typography.heading7.semibold14)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Body.semibold14", style = typography.body.semibold14)
            Text("Body.medium14", style = typography.body.medium14)
            Text("Body.regular14", style = typography.body.regular14)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Caption.medium12", style = typography.caption.medium12)
            Text("Caption.regular12", style = typography.caption.regular12)
        }
    }
}
