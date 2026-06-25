package org.app.core.extension

import androidx.compose.foundation.text.input.InputTransformation

/**
 * 최대 글자 수를 초과하는 입력을 차단하는 [InputTransformation]을 반환합니다.
 *
 * @param maxLength 허용할 최대 글자 수
 */
fun InputTransformation.Companion.maxLength(maxLength: Int): InputTransformation =
    InputTransformation {
        if (length > maxLength) {
            revertAllChanges()
        }
    }
