package org.app.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.style.MoballTextFieldInputStyle
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.maxLength
import org.app.core.extension.noRippleClickable

/**
 * 라인 텍스트 필드 (Line TextField)
 *
 * 한 줄 입력만 지원하며, 라벨·글자 수 카운터를 옵션으로 표시합니다.
 * 최대 [maxLength](기본 500)자까지 입력 가능합니다.
 *
 * @param state          텍스트 상태 관리 객체
 * @param placeholder    입력 전 안내 문구
 * @param modifier       외부 Modifier
 * @param label          상단 라벨 (null이면 미표시)
 * @param enabled        활성화 여부
 * @param isError        오류 상태 여부
 * @param errorMessage   오류 메시지 (null이면 미표시)
 * @param maxLength      최대 글자 수 (기본값: 20)
 * @param showCharCount  글자 수 카운터 표시 여부 (기본값: true)
 * @param keyboardOptions 키보드 옵션
 */
@Composable
fun MoballLineTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    maxLength: Int = 20,
    showCharCount: Boolean = true,
    inputTransformation: InputTransformation? = InputTransformation.maxLength(maxLength),
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val currentLength = state.text.length
    val isFilled = state.text.isNotEmpty()
    val inputStyle = MoballTextFieldInputStyle.from(
        isFilled = isFilled,
        isError = isError,
    )

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = MoballTheme.typography.caption.medium12,
                color = MoballTheme.colors.textPrimary,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                MoballBasicTextField(
                    state = state,
                    placeholder = placeholder,
                    placeholderColor = MoballTheme.colors.textTertiary,
                    placeholderStyle = inputStyle.getTextStyle(),
                    textColor = inputStyle.getTextColor(),
                    textStyle = inputStyle.getTextStyle(),
                    inputTransformation = inputTransformation,
                    keyboardOptions = keyboardOptions,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    isEnabled = enabled,
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            end = if (isFilled) 48.dp else 10.dp,
                        ).padding(
                            vertical = 10.dp,
                        ).onFocusEvent { isFocused = it.isFocused },
                    onKeyboardAction = { focusManager.clearFocus() },
                )

                HorizontalDivider(
                    thickness = 1.5.dp,
                    color = inputStyle.getLineBorderColor(),
                )
            }

            if (isFilled && enabled) {
                Icon(
                    imageVector = ImageVector.vectorResource(drawable.ic_close_sm),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                        .noRippleClickable {
                            state.edit { delete(0, length) }
                        },
                )
            }
        }

        val showBottom = (isError && errorMessage != null) || showCharCount
        if (showBottom) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isError && errorMessage != null) {
                    ErrorMessage(
                        message = errorMessage,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }

                if (showCharCount) {
                    Text(
                        text = "$currentLength / $maxLength",
                        style = inputStyle.getCountStyle(),
                        color = inputStyle.getCountColor(),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoballLineTextFieldPreview() {
    MoballTheme {
        Column(
            modifier = Modifier
                .background(MoballTheme.colors.backgroundBase)
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            // 입력 전 (라벨 있음)
            MoballLineTextField(
                state = rememberTextFieldState(),
                placeholder = "사용할 닉네임을 적어주세요",
                label = "닉네임",
            )
            // 입력 전 (라벨 없음)
            MoballLineTextField(
                state = rememberTextFieldState(),
                placeholder = "사용할 닉네임을 적어주세요",
            )
            // 입력 완료
            MoballLineTextField(
                state = rememberTextFieldState("홍길동"),
                placeholder = "사용할 닉네임을 적어주세요",
                label = "닉네임",
            )
            // 오류
            MoballLineTextField(
                state = rememberTextFieldState("홍길동!!!"),
                placeholder = "사용할 닉네임을 적어주세요",
                label = "닉네임",
                isError = true,
                errorMessage = "20자 이내로 입력해주세요.",
            )
        }
    }
}
