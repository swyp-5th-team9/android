package org.app.presentation.mypage

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.data.repository.api.AuthRepository
import org.app.data.repository.api.FavoriteRepository
import org.app.data.repository.api.UserRepository
import org.app.presentation.mypage.wishlist.WishlistItem
import org.app.presentation.pubdetail.model.KboTeamType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository,
        private val favoriteRepository: FavoriteRepository,
    ) : BaseViewModel<MyPageContract.State, MyPageContract.Event, MyPageContract.SideEffect>(
            MyPageContract.State(),
        ) {
        init {
            loadUser()
        }

        override fun onEvent(event: MyPageContract.Event) {
            when (event) {
                MyPageContract.Event.OnEditProfileClick ->
                    postSideEffect(MyPageContract.SideEffect.NavigateToEditProfile)

                MyPageContract.Event.OnWishlistClick ->
                    postSideEffect(MyPageContract.SideEffect.NavigateToWishlist)

                MyPageContract.Event.OnReportClick ->
                    postSideEffect(MyPageContract.SideEffect.NavigateToReport)

                MyPageContract.Event.OnWithdrawClick ->
                    postSideEffect(MyPageContract.SideEffect.NavigateToWithdraw)

                MyPageContract.Event.OnLogoutClick -> logout()

                MyPageContract.Event.OnRefresh -> loadUser()

                is MyPageContract.Event.OnApplyTeams -> applyTeams(event.teams)

                MyPageContract.Event.OnCopyEmailClick ->
                    postSideEffect(MyPageContract.SideEffect.ShowToast("이메일 주소가 복사되었습니다."))

                is MyPageContract.Event.OnPubClick ->
                    postSideEffect(MyPageContract.SideEffect.NavigateToPubDetail(event.pubId))
            }
        }

        private fun loadUser() {
            if (currentState.isLoading) return
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                // 서로 독립적인 요청이므로 병렬 실행
                val userDeferred = async { userRepository.getUser() }
                val favoriteDeferred = async { favoriteRepository.getFavorites() }
                val userResult = userDeferred.await()
                val favoriteResult = favoriteDeferred.await()

                userResult
                    .onSuccess { user ->
                        val favorites =
                            favoriteResult
                                .getOrElse { error ->
                                    Timber.w("즐겨찾기 조회 실패: $error")
                                    emptyList()
                                }.map {
                                    WishlistItem(
                                        favoriteId = it.favoriteId,
                                        pubId = it.pubId,
                                        pubName = it.pubName,
                                        address = it.address,
                                        thumbnailImageUrl = it.thumbnailImageUrl,
                                    )
                                }

                        setState {
                            copy(
                                isLoading = false,
                                nickname = user.nickname,
                                profileImageUrl = user.profileImageUrl,
                                supportedTeams = user.favoriteTeams.map { team -> team.teamName },
                                wishlistItems = favorites,
                            )
                        }
                    }.onFailure { error ->
                        setState { copy(isLoading = false) }
                        postSideEffect(MyPageContract.SideEffect.ShowToast("내 정보 조회에 실패했습니다."))
                        Timber.e("내 정보 조회 실패: $error")
                    }
            }
        }

        private fun applyTeams(teams: List<String>) {
            val teamIds = teams.map { shortName ->
                KboTeamType.fromShortName(shortName).id.toLong()
            }
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                userRepository
                    .patchUser(teamIds = teamIds)
                    .onSuccess {
                        setState { copy(isLoading = false, supportedTeams = teams) }
                    }.onFailure { error ->
                        setState { copy(isLoading = false) }
                        postSideEffect(MyPageContract.SideEffect.ShowToast("정보 수정에 실패했습니다."))
                        Timber.e("내 정보 수정 실패: $error")
                    }
            }
        }

        private fun logout() {
            viewModelScope.launch {
                authRepository
                    .logout()
                    .onSuccess {
                        postSideEffect(MyPageContract.SideEffect.NavigateToLogin)
                        Timber.d("로그아웃 성공")
                    }.onFailure { error ->
                        postSideEffect(MyPageContract.SideEffect.ShowToast("로그아웃에 실패했습니다."))
                        Timber.e("로그아웃 실패: $error")
                    }
            }
        }
    }
