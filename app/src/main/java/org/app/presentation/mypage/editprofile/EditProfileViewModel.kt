package org.app.presentation.mypage.editprofile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.UserRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : BaseViewModel<EditProfileContract.State, EditProfileContract.Event, EditProfileContract.SideEffect>(
            EditProfileContract.State(),
        ) {
        init {
            loadUser()
        }

        private fun loadUser() {
            viewModelScope.launch {
                userRepository
                    .getUser()
                    .onSuccess { user ->
                        setState {
                            copy(
                                originalNickname = user.nickname,
                                nickname = user.nickname,
                                originalProfileImageUrl = user.profileImageUrl,
                                profileImageUrl = user.profileImageUrl,
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("내 정보 조회 실패: $error")
                    }
            }
        }

        override fun onEvent(event: EditProfileContract.Event) {
            when (event) {
                is EditProfileContract.Event.OnNicknameChange -> {
                    val nicknameError = when {
                        event.nickname.length > 20 -> "20자 이내로 입력해주세요."
                        event.nickname.isBlank() -> "닉네임을 입력해주세요."
                        else -> null
                    }
                    setState { copy(nickname = event.nickname, nicknameError = nicknameError) }
                }

                is EditProfileContract.Event.OnProfileImageChange -> {
                    setState { copy(profileImageUrl = event.uri) }
                }

                EditProfileContract.Event.OnEditNicknameClick -> {
                    setState { copy(isEditingNickname = true) }
                }

                EditProfileContract.Event.OnSaveClick -> {
                    save()
                }
            }
        }

        private fun save() {
            if (currentState.isLoading) return
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                val trimmedNickname = currentState.nickname.trim()
                // 빈 닉네임은 "변경"으로 취급하지 않는다 — 사진만 바꾸는 저장을 막지 않기 위함
                val nicknameChanged = trimmedNickname.isNotBlank() &&
                    trimmedNickname != currentState.originalNickname
                val pickedImageUri = currentState.profileImageUrl
                    ?.takeIf { it != currentState.originalProfileImageUrl }

                if (!nicknameChanged && pickedImageUri == null) {
                    setState { copy(isLoading = false) }
                    if (trimmedNickname.isBlank()) {
                        postSideEffect(EditProfileContract.SideEffect.ShowToast("닉네임을 입력해 주세요."))
                    }
                    return@launch
                }

                // 닉네임·프로필 이미지를 multipart 한 번에 전송 (미전달 필드는 서버가 기존 값 유지)
                userRepository
                    .patchUser(
                        nickname = trimmedNickname.takeIf { nicknameChanged },
                        profileImageUri = pickedImageUri,
                    ).onSuccess {
                        setState {
                            copy(
                                isLoading = false,
                                originalNickname = if (nicknameChanged) trimmedNickname else originalNickname,
                                originalProfileImageUrl = profileImageUrl,
                            )
                        }
                        postSideEffect(EditProfileContract.SideEffect.ShowToast("정보가 수정됐어요."))
                        postSideEffect(EditProfileContract.SideEffect.NavigateBack)
                        Timber.d("내 정보 수정 성공")
                    }.onFailure { error ->
                        setState { copy(isLoading = false) }
                        postSideEffect(EditProfileContract.SideEffect.ShowToast("수정에 실패했습니다. 잠시 후 다시 시도해주세요."))
                        Timber.e("내 정보 수정 실패: $error")
                    }
            }
        }
    }
