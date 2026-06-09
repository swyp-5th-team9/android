package org.app.data.remote.datasource.api

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostKakaoLoginResponse

interface AuthRemoteDataSource {
    suspend fun postKakaoLogin(authorization: String): BaseResponse<PostKakaoLoginResponse>
}
