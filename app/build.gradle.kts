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
val naverClientId = properties.getProperty("naver_client_id")
    ?: providers.gradleProperty("NAVER_CLIENT_ID").orNull
    ?: throw org.gradle.api.GradleException("NAVER_CLIENT_ID가 설정되지 않았습니다.")

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
        manifestPlaceholders["NAVER_CLIENT_ID"] = naverClientId
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("prod.base.url") ?: ""}\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("prod.base.url") ?: ""}\"")
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

// Debug
    debugImplementation(libs.bundles.debug)

// Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.timber)
    implementation(libs.lottie.compose)

// 네이버 지도 SDK
    implementation(libs.naver.map.sdk)
    implementation(libs.google.play.services.location)
}
