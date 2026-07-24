package org.app.presentation.home.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naver.maps.map.overlay.OverlayImage
import org.app.core.designsystem.theme.MoballTheme

/**
 * 줌아웃 시 표시되는 클러스터 마커 (Compose 미리보기용)
 * 실제 NaverMap 에는 [createClusterMarkerBitmap] 으로 생성한 Bitmap 사용
 */
@Composable
fun PubClusterPin(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 117.dp, height = 115.dp)
            .clip(CircleShape)
            .background(MoballTheme.colors.mapClusterBg),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .blur(10.dp)
                .clip(CircleShape)
                .background(MoballTheme.colors.mapClusterIconBg),
        )
        Text(
            text = count.toString(),
            style = MoballTheme.typography.heading5.semibold18,
            color = MoballTheme.colors.textPrimaryInverse,
        )
    }
}

/**
 * NaverMap Marker에 사용할 클러스터 비트맵 생성
 * @param context 컨텍스트
 * @param count   클러스터 내 펍 수
 * @param sizePx  비트맵 크기(픽셀, 기본 120)
 */
fun createClusterMarkerBitmap(
    context: Context,
    count: Int,
    sizePx: Int = context.resources.displayMetrics.density
        .times(120)
        .toInt(),
): Bitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val radius = sizePx / 2f

    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color(0x473D4652).toArgb()
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, radius, bgPaint)

    val iconBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color(0xB23D4652).toArgb()
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, radius * 0.33f, iconBgPaint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = sizePx * 0.18f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    val textY = cy - (textPaint.descent() + textPaint.ascent()) / 2
    canvas.drawText(count.toString(), cx, textY, textPaint)

    return bitmap
}

// 클러스터 마커는 count 가 같으면 비트맵이 동일하므로 count 로 캐싱해
// 카메라 이동(idle)마다 반복되는 Canvas 드로잉/비트맵 생성을 메인 스레드에서 줄인다.
private val clusterOverlayImageCache = mutableMapOf<Int, OverlayImage>()

/** OverlayImage for NaverMap cluster marker */
fun clusterOverlayImage(
    context: Context,
    count: Int,
): OverlayImage =
    clusterOverlayImageCache.getOrPut(count) {
        OverlayImage.fromBitmap(createClusterMarkerBitmap(context, count))
    }

@Preview(showBackground = true, backgroundColor = 0xFFBFC6CF)
@Composable
private fun PinPreview() {
    MoballTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            PubClusterPin(count = 3)
        }
    }
}
