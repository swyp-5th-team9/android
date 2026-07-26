package org.app.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R.drawable
import org.app.core.designsystem.component.textfield.MoballBasicTextField
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun SearchDetailTextField(
    state: TextFieldState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "펍 이름, 구단을 입력해 주세요.",
    onSearch: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val isFilled = state.text.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MoballTheme.colors.backgroundSurface,
                shape = RoundedCornerShape(10.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(drawable.ic_chevron_left),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .noRippleClickable(onBack)
                .padding(all = 12.dp),
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(modifier = Modifier.weight(1f)) {
            MoballBasicTextField(
                state = state,
                placeholder = placeholder,
                placeholderColor = MoballTheme.colors.textTertiary,
                placeholderStyle = MoballTheme.typography.body.medium16,
                textColor = MoballTheme.colors.textPrimary,
                textStyle = MoballTheme.typography.body.medium16,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                lineLimits = TextFieldLineLimits.SingleLine,
                contentAlignment = Alignment.CenterStart,
                onKeyboardAction = {
                    onSearch?.invoke()
                    focusManager.clearFocus()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isFilled) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = ImageVector.vectorResource(drawable.ic_search_close_circle),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .noRippleClickable { state.edit { delete(0, length) } },
            )
        }

        Icon(
            imageVector = ImageVector.vectorResource(drawable.ic_home_search),
            contentDescription = null,
            tint = MoballTheme.colors.iconPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchDetailTextFieldDefaultPreview() {
    MoballTheme {
        SearchDetailTextField(
            state = rememberTextFieldState(),
            onBack = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchDetailTextFieldFilledPreview() {
    MoballTheme {
        SearchDetailTextField(
            state = rememberTextFieldState("잠실 맥주집"),
            onBack = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
