import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.services)
}

val properties = Properties().apply {
    val localProps = rootProject.file("local.properties")
    if (localProps.exists()) {
        localProps.inputStream().use { load(it) }
    }
}
val naverMapClientId = properties.getProperty("naver_map_client_id")
    ?: properties.getProperty("naver_client_id") // 하위 호환성 위해 유지
    ?: providers.gradleProperty("NAVER_MAP_CLIENT_ID").orNull
    ?: providers.gradleProperty("NAVER_CLIENT_ID").orNull
    ?: System.getenv("NAVER_MAP_CLIENT_ID")
    ?: System.getenv("NAVER_CLIENT_ID")
    ?: ""
val naverLoginClientId = properties.getProperty("naver_login_client_id")
    ?: providers.gradleProperty("NAVER_LOGIN_CLIENT_ID").orNull
    ?: System.getenv("NAVER_LOGIN_CLIENT_ID")
    ?: ""
val naverLoginClientSecret = properties.getProperty("naver_login_client_secret")
    ?: providers.gradleProperty("NAVER_LOGIN_CLIENT_SECRET").orNull
    ?: System.getenv("NAVER_LOGIN_CLIENT_SECRET")
    ?: ""
val naverLoginClientName = properties.getProperty("naver_login_client_name")
    ?: providers.gradleProperty("NAVER_LOGIN_CLIENT_NAME").orNull
    ?: System.getenv("NAVER_LOGIN_CLIENT_NAME")
    ?: ""

// 릴리즈 서명(upload keystore) — local.properties 또는 CI 환경변수에서 읽는다. 키 파일/비밀번호는 git 커밋 금지.
val releaseStoreFile = properties.getProperty("keystore.file") ?: System.getenv("KEYSTORE_FILE")
val releaseStorePassword = properties.getProperty("keystore.password") ?: System.getenv("KEYSTORE_PASSWORD")
val releaseKeyAlias = properties.getProperty("key.alias") ?: System.getenv("KEY_ALIAS")
val releaseKeyPassword = properties.getProperty("key.password") ?: System.getenv("KEY_PASSWORD")

// 4개 값이 모두 채워지고 keystore 파일이 실제로 존재할 때만 릴리즈 서명을 구성/연결한다.
// (일부만 채워진 채 signingConfig를 연결하면 AGP가 릴리즈 빌드에서 서명 검증 실패를 냄)
val hasReleaseSigning = !releaseStoreFile.isNullOrBlank() &&
    !releaseStorePassword.isNullOrBlank() &&
    !releaseKeyAlias.isNullOrBlank() &&
    !releaseKeyPassword.isNullOrBlank() &&
    file(releaseStoreFile).isFile

android {
    namespace = "com.moball.app"
    compileSdk = libs.versions.compileSdk
        .get()
        .toInt()

    signingConfigs {
        create("release") {
            // keystore 정보가 갖춰졌을 때만 서명 설정. 없으면 릴리즈는 미서명으로 빌드된다.
            if (hasReleaseSigning) {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    defaultConfig {
        applicationId = "com.moball.app"
        minSdk = libs.versions.minSdk
            .get()
            .toInt()
        targetSdk = libs.versions.targetSdk
            .get()
            .toInt()
        versionCode = libs.versions.versionCode
            .get()
            .toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["NAVER_MAP_CLIENT_ID"] = naverMapClientId
        buildConfigField("String", "NAVER_LOGIN_CLIENT_ID", "\"$naverLoginClientId\"")
        buildConfigField("String", "NAVER_LOGIN_CLIENT_SECRET", "\"$naverLoginClientSecret\"")
        buildConfigField("String", "NAVER_LOGIN_CLIENT_NAME", "\"$naverLoginClientName\"")

        val kakaoAppKey = properties.getProperty("kakao.app.key") ?: ""
        buildConfigField("String", "KAKAO_APP_KEY", "\"$kakaoAppKey\"")

        manifestPlaceholders["KAKAO_APP_KEY"] = kakaoAppKey
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("prod.base.url") ?: ""}\"")
            buildConfigField("Boolean", "USE_MOCK_SERVER", "false")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("prod.base.url") ?: ""}\"")
            buildConfigField("Boolean", "USE_MOCK_SERVER", "false")
            // keystore가 완전히 갖춰졌을 때만 릴리즈 서명 연결. 없으면 미서명으로 빌드(디버그/CI에서 검증 실패 방지).
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Compose 컴파일러 안정성/skippability 리포트 (build/compose_compiler 에 출력)
// 측정 전용 — 릴리스 산출물에는 영향 없음. `./gradlew :app:assembleRelease` 후 리포트 확인.
composeCompiler {
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
ktlint {
    android = true
    coloredOutput = true
    verbose = true
    outputToConsole = true
}

dependencies {
    // Androidx
    implementation(libs.bundles.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.datastore.preferences)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Kotlinx
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.immutable)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Network
    implementation(libs.bundles.network)

    // DI
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlinx.metadata.jvm)

    // Debug
    debugImplementation(libs.bundles.debug)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.timber)
    implementation(libs.lottie.compose)

    // Naver Map
    implementation(libs.naver.map.sdk)
    implementation(libs.google.play.services.location)

    // Kakao Login
    implementation(libs.kakao.sdk.user)

    // Naver Login
    implementation(libs.naver.login)

    // Firebase (Analytics, Messaging)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
}
