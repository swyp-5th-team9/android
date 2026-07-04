package org.app.presentation.mypage.editprofile

interface EditProfileContract {
    data class State(
        val originalNickname: String = "",
        val nickname: String = "",
        val originalProfileImageUrl: String? = null,
        val profileImageUrl: String? = null,
        val isEditingNickname: Boolean = false,
        val isLoading: Boolean = false,
        val nicknameError: String? = null,
    ) {
        val hasChanged: Boolean
            get() = (
                (nickname != originalNickname && nickname.isNotBlank()) ||
                    (profileImageUrl != originalProfileImageUrl)
            ) &&
                nicknameError == null
    }

    sealed interface Event {
        data class OnNicknameChange(
            val nickname: String,
        ) : Event

        data class OnProfileImageChange(
            val uri: String,
        ) : Event

        data object OnEditNicknameClick : Event

        data object OnSaveClick : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}
