package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostKakaoLoginResponse
import org.app.data.remote.dto.PostNaverLoginResponse

interface AuthRemoteDataSource {
    suspend fun postKakaoLogin(authorization: String): BaseResponse<PostKakaoLoginResponse>

    suspend fun postNaverLogin(authorization: String): BaseResponse<PostNaverLoginResponse>
}
