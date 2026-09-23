package org.app.presentation.notification

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.app.data.model.NotificationDeepLinkType

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

        /** 알림 카드 클릭 → 읽음 처리 (딥링크 이동은 후속 작업) */
        data class OnItemClick(
            val id: Long,
        ) : Event

        /** 알림 카드 더보기(삭제) */
        data class OnDeleteClick(
            val id: Long,
        ) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect

        /**
         * 알림 딥링크 → 홈 펍 리스트/지도로 이동.
         * [teamIds](경기 홈+원정)와 [businessDay](오늘/내일 요일 서버코드)를 필터로 적용한다.
         */
        data class NavigateToPubs(
            val teamIds: List<Long>,
            val businessDay: String?,
        ) : SideEffect

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
    // 딥링크 이동(후속 작업)에 필요한 값
    val matchId: Long = 0L,
    val teamIds: List<Long> = emptyList(),
    val deepLinkType: NotificationDeepLinkType = NotificationDeepLinkType.UNKNOWN,
)
