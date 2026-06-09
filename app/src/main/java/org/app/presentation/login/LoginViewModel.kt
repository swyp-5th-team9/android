package org.app.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.app.data.repository.api.AuthRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _sideEffect = MutableSharedFlow<LoginContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun fetchKakaoLogin(context: Context) =
            viewModelScope.launch {
                authRepository
                    .loginKakao(context = context)
                    .onSuccess { token ->
                        authRepository
                            .postKakaoLogin(authorization = token)
                            .onSuccess {
                                _sideEffect.emit(LoginContract.SideEffect.NavigateToHome)
                                Timber.tag("KakaoLogin").d("로그인 성공 $token")
                            }.onFailure { error ->
                                Timber.tag("KakaoLogin").e("로그인 실패 : $error")
                            }
                    }.onFailure { error ->
                        Timber.tag("KakaoLogin").e("로그인 실패 : $error")
                    }
            }
    }
