package org.app.data.local.datasource.api

/**
 * 프로필 이미지 로컬 저장소.
 *
 * TODO 서버에 프로필 이미지 업로드 API가 추가되면 서버 업로드로 교체하고,
 *  이 로컬 저장은 캐시 용도로만 사용하거나 제거한다.
 */
interface LocalProfileImageDataSource {
    /**
     * 갤러리 URI의 이미지를 압축해 내부 저장소에 저장한다.
     *
     * @return 저장된 파일의 절대 경로
     */
    suspend fun saveProfileImage(uriString: String): String

    /** 저장된 프로필 이미지 파일 경로. 없으면 null */
    suspend fun getProfileImagePath(): String?
}
