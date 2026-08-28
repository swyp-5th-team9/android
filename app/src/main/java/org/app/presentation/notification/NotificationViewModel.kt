package org.app.presentation.notification

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.app.core.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel
    @Inject
    constructor() :
    BaseViewModel<NotificationContract.State, NotificationContract.Event, NotificationContract.SideEffect>(
            NotificationContract.State(),
        ) {
        init {
            loadNotifications()
        }

        override fun onEvent(event: NotificationContract.Event) {
            when (event) {
                NotificationContract.Event.OnBackClick ->
                    postSideEffect(NotificationContract.SideEffect.NavigateBack)

                is NotificationContract.Event.OnDeleteClick -> {
                    setState {
                        copy(items = items.filterNot { it.id == event.id }.toImmutableList())
                    }
                    postSideEffect(NotificationContract.SideEffect.ShowToast("알림 삭제가 완료되었어요."))
                }
            }
        }

        private fun loadNotifications() {
            // TODO(알림): 서버 연동 전 임시 더미 데이터. 실제로는 알림 Repository에서 로드한다.
            setState {
                copy(
                    items = persistentListOf(
                        NotificationItem(
                            id = 1L,
                            title = "경기 일정 알림",
                            message = "LG 트윈스 경기가 오늘 오후 6시에 있어요!",
                            date = "8월 15일",
                        ),
                        NotificationItem(
                            id = 2L,
                            title = "경기 시작 알림",
                            message = "잠시 후 6시 30분에 경기가 시작돼요. 상영 펍을 확인해보세요!",
                            date = "8월 14일",
                        ),
                    ),
                )
            }
        }
    }
