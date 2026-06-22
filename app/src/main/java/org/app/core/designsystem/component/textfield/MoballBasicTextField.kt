package org.app.core.designsystem.component.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.theme.MoballTheme

/**
 * 경고 아이콘 + 에러 메시지를 가로로 표시합니다.
 * [MoballAreaTextField]·[MoballLineTextField] 내부에서 공통으로 사용됩니다.
 */
@Composable
internal fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = drawable.ic_warning),
            contentDescription = null,
            tint = MoballTheme.colors.stateNegative,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = message,
            style = MoballTheme.typography.caption.medium12,
            color = MoballTheme.colors.stateNegative,
        )
    }
}

/**
 * 기본 텍스트 필드 컴포넌트
 *
 * 장식이 없는 순수 입력 영역을 제공하며,
 * [MoballAreaTextField] 및 [MoballLineTextField]의 내부 입력 영역으로 사용됩니다.
 *
 * @param state              텍스트·커서·선택 상태를 관리하는 객체
 * @param textColor          입력 텍스트 색상
 * @param textStyle          입력 텍스트 스타일
 * @param placeholder        입력 전 안내 문구
 * @param placeholderColor   placeholder 색상
 * @param placeholderStyle   placeholder 스타일
 * @param modifier           Modifier
 * @param isEnabled          활성화 여부 (false → 입력 불가 + 시각적 비활성화)
 * @param isReadOnly         읽기 전용 여부 (입력 불가, 선택·복사는 가능)
 * @param keyboardOptions    키보드 옵션
 * @param onKeyboardAction   키보드 액션 핸들러
 * @param lineLimits         줄 수 제한 (SingleLine / MultiLine)
 * @param cursorColor        커서 색상 (기본값: textColor)
 * @param inputTransformation 입력 변환 (글자 수 제한 등)
 * @param outputTransformation 출력 변환
 * @param interactionSource  상호작용 소스 (Focus, Press 등)
 * @param trailingIcon       우측 끝 요소 (아이콘, 버튼 등)
 * @param contentAlignment   내부 콘텐츠 정렬
 */
@Composable
fun MoballBasicTextField(
    state: TextFieldState,
    textColor: Color,
    textStyle: TextStyle,
    placeholder: String,
    placeholderColor: Color,
    placeholderStyle: TextStyle,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    cursorColor: Color = textColor,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    trailingIcon: (@Composable () -> Unit)? = null,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = isEnabled,
        readOnly = isReadOnly,
        textStyle = textStyle.copy(color = textColor),
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(cursorColor),
        lineLimits = lineLimits,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        decorator = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = contentAlignment,
                ) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = placeholderStyle.copy(color = placeholderColor),
                        )
                    }
                    innerTextField()
                }
                trailingIcon?.invoke()
            }
        },
    )
}
