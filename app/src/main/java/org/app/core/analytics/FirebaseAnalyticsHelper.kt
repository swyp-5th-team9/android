package org.app.core.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Analytics 기반 [AnalyticsHelper] 구현.
 * 설치/DAU/리텐션/세션 등 표준 지표는 SDK가 자동 수집하고, 여기선 커스텀 이벤트만 전송한다.
 */
@Singleton
class FirebaseAnalyticsHelper
    @Inject
    constructor(
        private val firebaseAnalytics: FirebaseAnalytics,
    ) : AnalyticsHelper {
        override fun logEvent(
            name: String,
            params: Map<String, Any>,
        ) {
            firebaseAnalytics.logEvent(name) {
                params.forEach { (key, value) ->
                    when (value) {
                        is String -> param(key, value)
                        is Long -> param(key, value)
                        is Int -> param(key, value.toLong())
                        is Double -> param(key, value)
                        is Float -> param(key, value.toDouble())
                        is Boolean -> param(key, value.toString())
                        else -> param(key, value.toString())
                    }
                }
            }
        }
    }
