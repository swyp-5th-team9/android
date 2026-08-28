package org.app.presentation.notification

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface NotificationContract {
    data class State(
        val items: ImmutableList<NotificationItem> = persistentListOf(),
        val isLoading: Boolean = false,
    ) {
        /** 로딩이 끝났고 알림이 하나도 없는 상태 */
        val isEmpty: Boolean get() = !isLoading && items.isEmpty()
    }

    sealed interface Event {
        data object OnBackClick : Event

        /** 알림 카드 더보기(삭제) */
        data class OnDeleteClick(
            val id: Long,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        data class ShowToast(
            val message: String,
        ) : SideEffect
    }
}

/** 알림 목록 아이템 (표시용 모델) */
data class NotificationItem(
    val id: Long,
    val title: String, // 알림 유형 제목. 예: "경기 일정 알림"
    val message: String, // 본문. 예: "LG 트윈스 경기가 오늘 오후 6시에 있어요!"
    val date: String, // 표시용 날짜. 예: "8월 15일"
    val isRead: Boolean = false,
)
