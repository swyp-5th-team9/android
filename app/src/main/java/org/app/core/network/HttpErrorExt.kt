package org.app.core.network

import retrofit2.HttpException

/** HTTP 409 Conflict 여부 (예: 이미 즐겨찾기된 펍 재등록) */
fun Throwable.isHttpConflict(): Boolean = this is HttpException && code() == 409
