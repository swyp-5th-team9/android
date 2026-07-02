package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: T? = null,
    @SerialName("message")
    val message: String? = null,
)

fun <T : Any> BaseResponse<T>.getDataOrThrow(): T {
    if (!success) throw IllegalStateException(message ?: "요청이 실패했습니다.")
    return data ?: throw IllegalStateException(message ?: "응답 데이터가 없습니다.")
}

fun BaseResponse<*>.checkSuccess() {
    if (!success) throw IllegalStateException(message ?: "요청이 실패했습니다.")
}
