package org.app.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.moball.app.R

/**
 * URL을 통해 이미지를 비동기로 로드하여 표시하는 Composable입니다.
 *
 * Preview 모드에서는 placeholder 이미지를 표시하고,
 * 실제 실행 시에는 네트워크 이미지를 로드합니다.
 *
 * @param url 로드할 이미지의 URL (또는 Int 리소스 ID)
 * @param modifier Composable에 적용할 Modifier
 * @param contentScale 이미지 스케일링 방식 (기본: Fit)
 * @param contentDescription 접근성을 위한 이미지 설명
 * @param placeholderRes 로딩 중이나 에러 발생 시 표시할 이미지 리소스 ID
 * @param bypassCache true면 Coil 메모리·디스크 캐시를 건너뛰고 항상 새로 로드합니다.
 *  서버가 동일한 URL(key)에 이미지를 덮어쓰는 경우(예: 프로필 이미지 — 사용자당 1개 관리)
 *  캐시가 이전 이미지를 계속 보여주는 문제를 방지합니다.
 */
@Composable
fun UrlImage(
    url: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    placeholderRes: Int? = null,
    bypassCache: Boolean = false,
) {
    if (LocalInspectionMode.current) {
        val imageRes = if (url is Int) url else placeholderRes ?: R.drawable.ic_launcher_background
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
        )
    } else {
        val model: Any? = if (bypassCache && url != null) {
            ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(url)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()
        } else {
            url ?: placeholderRes
        }
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            placeholder = placeholderRes?.let { painterResource(it) },
            error = placeholderRes?.let { painterResource(it) },
            fallback = placeholderRes?.let { painterResource(it) },
        )
    }
}

@Preview
@Composable
fun UrlImagePreview() {
    UrlImage(
        url = "",
        modifier = Modifier.size(100.dp),
    )
}
