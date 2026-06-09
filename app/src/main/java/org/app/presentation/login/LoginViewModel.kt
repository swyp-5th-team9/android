package org.app.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.app.data.repository.api.AuthRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val kakaoLoginManager: KakaoLoginManager,
        private val naverLoginManager: NaverLoginManager,
    ) : ViewModel() {
        private val _sideEffect = MutableSharedFlow<LoginContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun login(
            type: LoginContract.SocialType,
            context: Context,
        ) = viewModelScope.launch {
            val loginManager =
                when (type) {
                    LoginContract.SocialType.KAKAO -> kakaoLoginManager
                    LoginContract.SocialType.NAVER -> naverLoginManager
                }

            loginManager
                .login(context = context)
                .onSuccess { token ->
                    // Todo: 네이버 연동 시 Repository에 네이버 로그인 API 추가 필요
                    val loginResult =
                        when (type) {
                            LoginContract.SocialType.KAKAO -> authRepository.postKakaoLogin(authorization = token)
                            LoginContract.SocialType.NAVER -> {
                                // 임시로 카카오 API 사용 또는 에러 처리
                                authRepository.postKakaoLogin(authorization = token)
                            }
                        }

                    loginResult
                        .onSuccess {
                            _sideEffect.emit(LoginContract.SideEffect.ShowToast("${type.name} 로그인 성공"))
                            _sideEffect.emit(LoginContract.SideEffect.NavigateToHome)
                        }.onFailure { error ->
                            _sideEffect.emit(LoginContract.SideEffect.ShowToast("서버 로그인 실패: ${error.message}"))
                        }
                }.onFailure { error ->
                    _sideEffect.emit(LoginContract.SideEffect.ShowToast("${type.name} 로그인 실패: ${error.message}"))
                }
        }
    }
