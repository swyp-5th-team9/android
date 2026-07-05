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
                            )
                        }
                    }.onFailure { error ->
                        Timber.e("내 정보 조회 실패: $error")
                    }

                // TODO 서버 프로필 이미지 API 추가 시 서버 응답 값으로 교체
                userRepository
                    .getLocalProfileImagePath()
                    .getOrNull()
                    ?.let { path ->
                        setState {
                            copy(
                                originalProfileImageUrl = "file://$path",
                                profileImageUrl = "file://$path",
                            )
                        }
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
                if (trimmedNickname.isBlank()) {
                    setState { copy(isLoading = false) }
                    postSideEffect(EditProfileContract.SideEffect.ShowToast("닉네임을 입력해 주세요."))
                    return@launch
                }

                val nicknameChanged = trimmedNickname != currentState.originalNickname
                val pickedImageUri = currentState.profileImageUrl
                    ?.takeIf { it != currentState.originalProfileImageUrl }

                if (!nicknameChanged && pickedImageUri == null) {
                    setState { copy(isLoading = false) }
                    return@launch
                }

                // 프로필 이미지 저장 — 서버 API가 없어 내부 저장소에 압축 저장한다.
                // TODO 서버 프로필 이미지 업로드 API 추가 시 서버 업로드로 교체
                if (pickedImageUri != null) {
                    val imageResult = userRepository.saveLocalProfileImage(pickedImageUri)
                    val savedPath = imageResult.getOrNull()
                    if (savedPath == null) {
                        Timber.e(imageResult.exceptionOrNull(), "프로필 이미지 저장 실패")
                        setState { copy(isLoading = false) }
                        postSideEffect(EditProfileContract.SideEffect.ShowToast("프로필 사진 저장에 실패했습니다."))
                        return@launch
                    }
                    setState {
                        copy(
                            originalProfileImageUrl = "file://$savedPath",
                            profileImageUrl = "file://$savedPath",
                        )
                    }
                }

                if (nicknameChanged) {
                    userRepository
                        .patchUser(nickname = trimmedNickname)
                        .onSuccess {
                            setState { copy(isLoading = false, originalNickname = trimmedNickname) }
                            postSideEffect(EditProfileContract.SideEffect.ShowToast("정보가 수정됐어요."))
                            postSideEffect(EditProfileContract.SideEffect.NavigateBack)
                            Timber.d("내 정보 수정 성공")
                        }.onFailure { error ->
                            setState { copy(isLoading = false) }
                            postSideEffect(EditProfileContract.SideEffect.ShowToast("수정에 실패했습니다. 잠시 후 다시 시도해주세요."))
                            Timber.e("내 정보 수정 실패: $error")
                        }
                } else {
                    setState { copy(isLoading = false) }
                    postSideEffect(EditProfileContract.SideEffect.ShowToast("정보가 수정됐어요."))
                    postSideEffect(EditProfileContract.SideEffect.NavigateBack)
                }
            }
        }
    }
