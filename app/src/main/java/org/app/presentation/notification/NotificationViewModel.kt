package org.app.presentation.notification

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.app.core.common.base.BaseViewModel
import org.app.core.network.isHttpNotFound
import org.app.data.model.Notification
import org.app.data.model.NotificationDeepLinkType
import org.app.data.repository.api.NotificationRepository
import timber.log.Timber
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
private val KST_ZONE = ZoneId.of("Asia/Seoul")

// 서버 businessDay 요일 코드 (월~일). DayOfWeek.value(1=월..7=일) - 1 인덱스.
private val WEEKDAY_CODES = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

@HiltViewModel
class NotificationViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) : BaseViewModel<NotificationContract.State, NotificationContract.Event, NotificationContract.SideEffect>(
            NotificationContract.State(),
        ) {
        init {
            loadNotifications()
        }

        override fun onEvent(event: NotificationContract.Event) {
            when (event) {
                NotificationContract.Event.OnBackClick ->
                    postSideEffect(NotificationContract.SideEffect.NavigateBack)

                is NotificationContract.Event.OnItemClick -> onItemClick(event.id)

                is NotificationContract.Event.OnDeleteClick -> deleteNotification(event.id)
            }
        }

        private fun loadNotifications() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                notificationRepository
                    .getNotifications()
                    .onSuccess { notifications ->
                        setState {
                            copy(
                                items = notifications.map { it.toNotificationItem() }.toImmutableList(),
                                isLoading = false,
                            )
                        }
                    }.onFailure {
                        Timber.e("알림 목록 로드 실패: $it")
                        setState { copy(isLoading = false) }
                    }
            }
        }

        /** 카드 클릭: 읽음 처리 후 deepLinkType에 맞는 화면으로 이동한다. */
        private fun onItemClick(id: Long) {
            val item = currentState.items.firstOrNull { it.id == id } ?: return
            markAsRead(item)
            navigateForDeepLink(item)
        }

        /** 알림 읽음 처리 (멱등). 로컬 상태를 먼저 갱신하고 서버에 반영한다. */
        private fun markAsRead(item: NotificationItem) {
            if (!item.isRead) {
                setState {
                    copy(items = items.map { if (it.id == item.id) it.copy(isRead = true) else it }.toImmutableList())
                }
            }
            viewModelScope.launch {
                notificationRepository
                    .readNotification(item.id)
                    .onFailure { Timber.e("알림 읽음 처리 실패: $it") }
            }
        }

        /** deepLinkType별 이동. 미정의(UNKNOWN)는 이동하지 않는다. */
        private fun navigateForDeepLink(item: NotificationItem) {
            val businessDay = when (item.deepLinkType) {
                // 당일 경기 관련 → 오늘 요일
                NotificationDeepLinkType.TODAY_PUBS,
                NotificationDeepLinkType.NEARBY_PUBS,
                -> weekdayCodeOf(LocalDate.now(KST_ZONE))
                // 내일 경기 관련 → 내일 요일
                NotificationDeepLinkType.TOMORROW_GAME -> weekdayCodeOf(LocalDate.now(KST_ZONE).plusDays(1))
                NotificationDeepLinkType.UNKNOWN -> return
            }
            postSideEffect(
                NotificationContract.SideEffect.NavigateToPubs(
                    teamIds = item.teamIds,
                    businessDay = businessDay,
                ),
            )
        }

        private fun deleteNotification(id: Long) {
            viewModelScope.launch {
                notificationRepository
                    .deleteNotification(id)
                    .onSuccess { removeItem(id) }
                    .onFailure { throwable ->
                        // 이미 삭제된 알림(404)은 성공과 동일하게 목록에서 제거한다.
                        if (throwable.isHttpNotFound()) {
                            removeItem(id)
                        } else {
                            Timber.e("알림 삭제 실패: $throwable")
                            postSideEffect(NotificationContract.SideEffect.ShowToast("알림 삭제에 실패했어요."))
                        }
                    }
            }
        }

        private fun removeItem(id: Long) {
            setState { copy(items = items.filterNot { it.id == id }.toImmutableList()) }
            postSideEffect(NotificationContract.SideEffect.ShowToast("알림 삭제가 완료되었어요."))
        }
    }

private fun Notification.toNotificationItem(): NotificationItem =
    NotificationItem(
        id = id,
        title = title,
        message = content,
        date = createdAt?.toDisplayDate().orEmpty(),
        isRead = isRead,
        matchId = matchId,
        teamIds = teamIds,
        deepLinkType = deepLinkType,
    )

// TODO(#69): 기획 Q2(발송 시점 기준 표기 규칙) 확정되면 상대 시간 등으로 세분화
private fun OffsetDateTime.toDisplayDate(): String = format(DATE_FORMATTER)

private fun weekdayCodeOf(date: LocalDate): String = WEEKDAY_CODES[date.dayOfWeek.value - 1]
