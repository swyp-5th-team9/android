package org.app.data.remote.service

import org.app.data.remote.dto.BaseResponse
import org.app.data.remote.dto.PostRefreshTokenRequest
import org.app.data.remote.dto.PostRefreshTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * refresh-token 전용 서비스.
 * TokenAuthenticator 안에서 호출되므로 반드시 authenticator가 없는
 * 별도 OkHttpClient(@RefreshRetrofit)로 통신해야 한다. (데드락 방지)
 */
interface TokenRefreshService {
    @POST("/api/v1/auth/refresh-token")
    suspend fun postRefreshToken(
        @Body body: PostRefreshTokenRequest,
    ): BaseResponse<PostRefreshTokenResponse>
}
