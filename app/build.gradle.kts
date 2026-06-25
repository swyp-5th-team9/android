import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    // TODO alias(libs.plugins.google.services)
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

android {
    namespace = "com.moball.app"
    compileSdk = libs.versions.compileSdk
        .get()
        .toInt()

    signingConfigs {
        create("release") {
            // TODO: Add signing config details
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
            buildConfigField("Boolean", "USE_MOCK_SERVER", "true")
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
            signingConfig = signingConfigs.getByName("release")
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
}
