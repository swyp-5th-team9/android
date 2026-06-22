package org.app.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.maxLength

/** 장문 텍스트필드 기본 최대 글자 수 */
private const val AREA_MAX_LENGTH = 500

/**
 * 장문 텍스트 필드 (Area TextField)
 *
 * 피그마 컴포넌트: `텍스트필드-장문`
 *  - 상태=입력전  : 테두리 borderNormal(#EBEDF0), placeholder 표시
 *  - 상태=입력완료: 테두리 borderNormal(#EBEDF0), 입력 텍스트 표시
 *  - 상태=오류    : 테두리 stateNegative(#E53234), 에러 아이콘 + 메시지 표시
 *
 * 여러 줄 입력을 지원하며 최대 [maxLength](기본 500)자까지 입력 가능합니다.
 * 우측 하단에 `현재 글자 수 / 최대 글자 수` 카운터가 항상 표시됩니다.
 *
 * @param state        텍스트 상태 관리 객체
 * @param placeholder  입력 전 안내 문구
 * @param modifier     외부 Modifier
 * @param enabled      활성화 여부
 * @param isError      오류 상태 여부
 * @param errorMessage 오류 메시지 (null이면 미표시)
 * @param maxLength    최대 글자 수 (기본값: 500)
 * @param keyboardOptions 키보드 옵션
 */
@Composable
fun MoballAreaTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    maxLength: Int = AREA_MAX_LENGTH,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = inputStyle.getAreaBorderColor(),
                    shape = RoundedCornerShape(12.dp),
                ).background(
                    color = MoballTheme.colors.backgroundBase,
                ).padding(16.dp),
        ) {
            MoballBasicTextField(
                state = state,
                placeholder = placeholder,
                placeholderColor = MoballTheme.colors.textTertiary,
                placeholderStyle = inputStyle.getTextStyle(),
                textColor = inputStyle.getTextColor(),
                textStyle = inputStyle.getTextStyle(),
                inputTransformation = InputTransformation.maxLength(maxLength),
                keyboardOptions = keyboardOptions,
                lineLimits = TextFieldLineLimits.MultiLine(),
                isEnabled = enabled,
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .onFocusEvent { isFocused = it.isFocused },
                onKeyboardAction = { focusManager.clearFocus() },
            )
            Text(
                text = "$currentLength / $maxLength",
                style = inputStyle.getCountStyle(),
                color = inputStyle.getCountColor(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End),
            )
        }

        if (isError && errorMessage != null) {
            ErrorMessage(
                message = errorMessage,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoballAreaTextFieldPreview() {
    MoballTheme {
        Column(
            modifier = Modifier
                .background(MoballTheme.colors.backgroundBase)
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 입력 전
            MoballAreaTextField(
                state = rememberTextFieldState(),
                placeholder = "예: 영업시간이 실제와 달라요.",
            )
            // 입력 완료
            MoballAreaTextField(
                state = rememberTextFieldState("영업시간이 실제와 달라요!!!"),
                placeholder = "예: 영업시간이 실제와 달라요.",
            )
            // 오류
            MoballAreaTextField(
                state = rememberTextFieldState("비속어 포함 텍스트"),
                placeholder = "예: 영업시간이 실제와 달라요.",
                isError = true,
                errorMessage = "비속어는 사용할 수 없어요.",
            )
        }
    }
}
