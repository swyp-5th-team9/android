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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(EditProfileContract.State())
        val state = _state.asStateFlow()

        private val _sideEffect = MutableSharedFlow<EditProfileContract.SideEffect>()
        val sideEffect = _sideEffect.asSharedFlow()

        fun onEvent(event: EditProfileContract.Event) {
            when (event) {
                is EditProfileContract.Event.OnNicknameChange -> {
                    _state.update { it.copy(nickname = event.nickname) }
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
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    // TODO: API 연결
                    Timber.d("닉네임 변경: ${_state.value.nickname}")
                    _sideEffect.emit(EditProfileContract.SideEffect.ShowToast("정보가 수정됐어요."))
                    _sideEffect.emit(EditProfileContract.SideEffect.NavigateBack)
                } catch (e: Exception) {
                    _sideEffect.emit(EditProfileContract.SideEffect.ShowToast("수정 실패: ${e.message}"))
                    Timber.e("닉네임 변경 실패: $e")
                } finally {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
