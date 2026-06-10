package org.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.moball.app.BuildConfig
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MoballApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKakaoSdk()
        initNaverSdk()
        initTimber()
        setDarkMode()
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
