package org.app.data.mapper

import org.app.data.model.KakaoLoginToken
import org.app.data.remote.dto.PostKakaoLoginResponse

fun PostKakaoLoginResponse.toKakaoLoginToken() =
    KakaoLoginToken(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
    )
