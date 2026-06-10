package org.app.data.local.datasource.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.app.core.local.datastore.di.TokenDataStore
import org.app.data.local.datasource.api.LocalTokenDataSource
import java.io.IOException
import javax.inject.Inject

class LocalTokenDataSourceImpl
    @Inject
    constructor(
        @TokenDataStore private val dataStore: DataStore<Preferences>,
    ) : LocalTokenDataSource {
        override suspend fun getAccessToken(): String? =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) emit(emptyPreferences()) else throw exception
                }.map { prefs ->
                    prefs[ACCESS_TOKEN]
                }.firstOrNull()

        override suspend fun getRefreshToken(): String? =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) emit(emptyPreferences()) else throw exception
                }.map { prefs ->
                    prefs[REFRESH_TOKEN]
                }.firstOrNull()

        override suspend fun getLoginType(): String? =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) emit(emptyPreferences()) else throw exception
                }.map { prefs ->
                    prefs[LOGIN_TYPE]
                }.firstOrNull()

        override suspend fun setAccessToken(accessToken: String) {
            dataStore.edit { prefs ->
                prefs[ACCESS_TOKEN] = accessToken
            }
        }

        override suspend fun setRefreshToken(refreshToken: String) {
            dataStore.edit { prefs ->
                prefs[REFRESH_TOKEN] = refreshToken
            }
        }

        override suspend fun setLoginType(loginType: String) {
            require(loginType in ALLOWED_LOGIN_TYPES) {
                "Unsupported login type: $loginType"
            }
            dataStore.edit { prefs ->
                prefs[LOGIN_TYPE] = loginType
            }
        }

        override suspend fun clearTokens() {
            dataStore.edit { prefs ->
                prefs.remove(ACCESS_TOKEN)
                prefs.remove(REFRESH_TOKEN)
                prefs.remove(LOGIN_TYPE)
            }
        }

        companion object {
            private val ALLOWED_LOGIN_TYPES = setOf("KAKAO", "NAVER")
            private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
            private val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
            private val LOGIN_TYPE = stringPreferencesKey("LOGIN_TYPE")
        }
    }
