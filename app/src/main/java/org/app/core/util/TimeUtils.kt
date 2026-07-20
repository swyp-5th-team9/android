package org.app.core.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    private val KST_ZONE = ZoneId.of("Asia/Seoul")
    private val UTC_ZONE = ZoneId.of("UTC")

    /**
     * UTC 시간 문자열(HH:mm:ss 또는 HH:mm)을 KST LocalTime으로 변환
     */
    fun parseUtcToKst(timeStr: String?): LocalTime? {
        if (timeStr == null) return null
        return runCatching {
            val rawTime = LocalTime.parse(timeStr.take(5), TIME_FORMATTER)
            // 시간만 있을 경우 날짜는 임의로 고정하여 변환 (오늘 날짜 기준)
            val today = java.time.LocalDate.now()
            val zonedDateTime = LocalDateTime
                .of(today, rawTime)
                .atZone(UTC_ZONE)
                .withZoneSameInstant(KST_ZONE)
            zonedDateTime.toLocalTime()
        }.getOrNull()
    }

    /**
     * LocalTime을 "HH:mm" 형식의 문자열로 변환
     */
    fun formatTime(time: LocalTime?): String = time?.format(TIME_FORMATTER) ?: ""

    /**
     * 현재 시각(KST). 영업 여부 판정 등 businessHours(KST 변환됨)와 비교할 때 사용.
     */
    fun nowKst(): LocalDateTime = LocalDateTime.now(KST_ZONE)
}
