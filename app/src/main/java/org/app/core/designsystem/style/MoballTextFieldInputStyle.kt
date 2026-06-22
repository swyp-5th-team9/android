package org.app.core.designsystem.style

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import org.app.core.designsystem.theme.MoballTheme

/**
 * 텍스트 필드 입력 상태별 스타일 정의
 *
 * | 상태    | 설명                                |
 * |---------|-------------------------------------|
 * | DEFAULT | 입력 전 — placeholder 표시          |
 * | FILLED  | 입력 완료 — 텍스트가 있는 상태      |
 * | ERROR   | 오류 — 빨간 테두리 + 에러 메시지   |
 */
enum class MoballTextFieldInputStyle {
    DEFAULT,
    FILLED,
    ERROR,
    ;

    companion object {
        fun from(
            isFilled: Boolean,
            isError: Boolean,
        ): MoballTextFieldInputStyle =
            when {
                isError -> ERROR
                isFilled -> FILLED
                else -> DEFAULT
            }
    }

    /** 박스 테두리 색상 */
    @Composable
    @ReadOnlyComposable
    fun getAreaBorderColor(): Color =
        when (this) {
            DEFAULT, FILLED -> MoballTheme.colors.borderNormal
            ERROR -> MoballTheme.colors.stateNegative
        }

    /** 하단 라인 색상 */
    @Composable
    @ReadOnlyComposable
    fun getLineBorderColor(): Color =
        when (this) {
            DEFAULT -> MoballTheme.colors.borderStrong
            FILLED -> MoballTheme.colors.textPrimary
            ERROR -> MoballTheme.colors.stateNegative
        }

    /** 입력 텍스트 색상 */
    @Composable
    @ReadOnlyComposable
    fun getTextColor(): Color =
        when (this) {
            DEFAULT -> MoballTheme.colors.textTertiary
            FILLED, ERROR -> MoballTheme.colors.textPrimary
        }

    /** 글자 수 카운터 색상 */
    @Composable
    @ReadOnlyComposable
    fun getCountColor(): Color =
        when (this) {
            DEFAULT, FILLED -> MoballTheme.colors.textSecondary
            ERROR -> MoballTheme.colors.stateNegative
        }

    /** 본문 텍스트 스타일*/
    @Composable
    @ReadOnlyComposable
    fun getTextStyle(): TextStyle = MoballTheme.typography.body.medium14

    /** 카운터 텍스트 스타일*/
    @Composable
    @ReadOnlyComposable
    fun getCountStyle(): TextStyle = MoballTheme.typography.caption.regular12
}
