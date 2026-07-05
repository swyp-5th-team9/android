package org.app.data.local.datasource.impl

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.app.core.util.ImageCompressor
import org.app.data.local.datasource.api.LocalProfileImageDataSource
import java.io.File
import javax.inject.Inject

private const val PROFILE_DIR = "profile"
private const val PROFILE_IMAGE_MAX_DIMENSION = 512

class LocalProfileImageDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : LocalProfileImageDataSource {
        private val profileDir: File
            get() = File(context.filesDir, PROFILE_DIR)

        override suspend fun saveProfileImage(uriString: String): String =
            withContext(Dispatchers.IO) {
                profileDir.mkdirs()
                // 저장할 때마다 새 파일명을 사용해 Coil 이미지 캐시가 갱신되도록 한다.
                val target = File(profileDir, "profile_${System.currentTimeMillis()}.jpg")
                ImageCompressor.compress(
                    context = context,
                    uri = uriString.toUri(),
                    targetFile = target,
                    maxDimension = PROFILE_IMAGE_MAX_DIMENSION,
                )
                profileDir
                    .listFiles()
                    ?.filter { it.name != target.name }
                    ?.forEach { it.delete() }
                target.absolutePath
            }

        override suspend fun getProfileImagePath(): String? =
            withContext(Dispatchers.IO) {
                profileDir
                    .listFiles()
                    ?.maxByOrNull { it.name }
                    ?.absolutePath
            }
    }
