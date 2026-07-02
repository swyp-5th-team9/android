package org.app.presentation.mypage.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.data.repository.api.UserRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(EditProfileContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<EditProfileContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        init {
            loadUser()
        }

        private fun loadUser() {
            viewModelScope.launch {
                userRepository
                    .getUser()
                    .onSuccess { user ->
                        _state.update {
                            it.copy(
                                originalNickname = user.nickname,
                                nickname = user.nickname,
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("내 정보 조회 실패: $error")
                    }
            }
        }

        fun onEvent(event: EditProfileContract.Event) {
            when (event) {
                is EditProfileContract.Event.OnNicknameChange -> {
                    val nicknameError = when {
                        event.nickname.length >= 20 -> "20자 이내로 입력해주세요."
                        event.nickname.isBlank() -> "닉네임을 입력해주세요."
                        else -> null
                    }
                    _state.update { it.copy(nickname = event.nickname, nicknameError = nicknameError) }
                }

                is EditProfileContract.Event.OnProfileImageChange -> {
                    _state.update { it.copy(profileImageUrl = event.uri) }
                }

                EditProfileContract.Event.OnEditNicknameClick -> {
                    _state.update { it.copy(isEditingNickname = true) }
                }

                EditProfileContract.Event.OnSaveClick -> {
                    save()
                }
            }
        }

        private fun save() {
            if (_state.value.isLoading) return
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val trimmedNickname = _state.value.nickname.trim()
                if (trimmedNickname.isBlank()) {
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(EditProfileContract.SideEffect.ShowToast("닉네임을 입력해 주세요."))
                    return@launch
                }
                val nickname = trimmedNickname.takeIf { it != _state.value.originalNickname }
                    ?: run {
                        _state.update { it.copy(isLoading = false) }
                        return@launch
                    }

                userRepository
                    .patchUser(nickname = nickname)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(EditProfileContract.SideEffect.ShowToast("정보가 수정됐어요."))
                        _sideEffect.emit(EditProfileContract.SideEffect.NavigateBack)
                        Timber.d("내 정보 수정 성공")
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.emit(EditProfileContract.SideEffect.ShowToast("수정 실패: ${error.message}"))
                        Timber.e("내 정보 수정 실패: $error")
                    }
            }
        }
    }
