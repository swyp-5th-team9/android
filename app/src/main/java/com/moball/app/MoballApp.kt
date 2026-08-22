package com.moball.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp
import org.app.core.notification.createNotificationChannels
import timber.log.Timber

@HiltAndroidApp
class MoballApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKakaoSdk()
        initNaverSdk()
        initTimber()
        setDarkMode()
        initNotificationChannels()
    }

    private fun initNotificationChannels() {
        // 알림 채널 등록 (토큰 sync는 로그인 성공 후 각 ViewModel에서 수행).
        createNotificationChannels(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun initKakaoSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }

    private fun initNaverSdk() {
        NidOAuth.initialize(
            this,
            BuildConfig.NAVER_LOGIN_CLIENT_ID,
            BuildConfig.NAVER_LOGIN_CLIENT_SECRET,
            BuildConfig.NAVER_LOGIN_CLIENT_NAME,
        )
    }
}
