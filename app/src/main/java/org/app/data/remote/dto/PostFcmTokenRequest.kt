package org.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * FCM 디바이스 토큰 등록/갱신 요청 바디.
 *
 * TODO(#알림): 백엔드 스펙 확정 시 필드명/구조 확인. 필요하면 platform, deviceId 등 추가.
 */
@Serializable
data class PostFcmTokenRequest(
    @SerialName("token")
    val token: String,
)
