# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===== 크래시 스택트레이스 디버깅용 (라인 번호 유지) =====
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== 네이버 로그인 SDK =====
-keep public class com.navercorp.nid.** { *; }
-keep public class com.nhn.android.naverlogin.** { *; }
-dontwarn com.navercorp.nid.**

# ===== 카카오 SDK =====
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keepattributes Signature
-keepattributes *Annotation*

# ===== kotlinx.serialization =====
-keepattributes InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# 앱 내 @Serializable 클래스 (DTO 등)
-keep,includedescriptorclasses class org.app.**$$serializer { *; }
-keepclassmembers class org.app.** {
    *** Companion;
}
-keepclasseswithmembers class org.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.moball.**$$serializer { *; }
-keepclassmembers class com.moball.** {
    *** Companion;
}
-keepclasseswithmembers class com.moball.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ===== Retrofit =====
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
