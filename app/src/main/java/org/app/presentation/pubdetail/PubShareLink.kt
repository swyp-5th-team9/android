package org.app.presentation.pubdetail

/**
 * 펍 공유 링크/문구 생성 헬퍼.
 *
 * TODO(#65): [BASE_URL]은 App Links용 도메인이 확정되면 교체한다.
 *  (도메인 + /.well-known/assetlinks.json 호스팅이 준비돼야 링크로 앱이 바로 열린다.)
 *  현재는 공유 UI/클립보드/시스템 공유 플로우 검증용 placeholder URL이다.
 */
object PubShareLink {
    private const val BASE_URL = "https://moball.app/pub"

    /** 펍 상세 딥링크 URL */
    fun url(pubId: Long): String = "$BASE_URL/$pubId"

    /** 다른 앱에 공유할 때 실릴 문구 (가게명 + 링크) */
    fun shareText(
        pubName: String,
        pubId: Long,
    ): String = "[모여볼] $pubName\n${url(pubId)}"
}
