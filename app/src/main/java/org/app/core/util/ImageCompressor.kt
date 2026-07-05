package org.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/**
 * 업로드 전 이미지 리사이즈 + JPEG 압축 유틸.
 *
 * 갤러리 원본(수 MB~수십 MB)을 그대로 업로드하면 전송이 수십 초씩 걸리므로,
 * 긴 변 기준 [maxDimension]px 이하로 줄이고 JPEG로 재인코딩한다.
 * [ImageDecoder]를 사용하므로 EXIF 회전이 자동 반영된다. (minSdk 28)
 */
object ImageCompressor {
    private const val DEFAULT_MAX_DIMENSION = 1080
    private const val DEFAULT_JPEG_QUALITY = 85

    /**
     * [uri]의 이미지를 압축하여 [targetFile]에 JPEG로 저장한다.
     *
     * @param maxDimension 긴 변 최대 픽셀
     * @param quality JPEG 품질 (0~100)
     * @return 저장된 [targetFile]
     */
    suspend fun compress(
        context: Context,
        uri: Uri,
        targetFile: File,
        maxDimension: Int = DEFAULT_MAX_DIMENSION,
        quality: Int = DEFAULT_JPEG_QUALITY,
    ): File =
        withContext(Dispatchers.IO) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            var bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                // Bitmap.compress를 쓰려면 소프트웨어 할당이 필요
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val maxSide = maxOf(info.size.width, info.size.height)
                if (maxSide > maxDimension) {
                    decoder.setTargetSampleSize(maxSide / maxDimension)
                }
            }

            // 샘플링(2의 배수 단위) 후에도 maxDimension을 넘으면 정밀 리사이즈
            val maxSide = maxOf(bitmap.width, bitmap.height)
            if (maxSide > maxDimension) {
                val scale = maxDimension.toFloat() / maxSide
                val scaled = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).roundToInt().coerceAtLeast(1),
                    (bitmap.height * scale).roundToInt().coerceAtLeast(1),
                    true,
                )
                if (scaled != bitmap) bitmap.recycle()
                bitmap = scaled
            }

            targetFile.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            bitmap.recycle()
            targetFile
        }
}
