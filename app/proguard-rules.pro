# © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
# ProGuard Rules for Reward Club App
# Production Release — Obfuscation & Security Hardening

# ─── Keep app entry points and custom package ─────────────────────────────────
-keep class com.rewardclub.app.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application

# ─── Protect sensitive classes from reverse engineering ──────────────────────
# EmailSender is intentionally NOT kept here — let ProGuard obfuscate it fully.
# Keeping it with {*;} would expose class/method names in the APK (opposite of intent).
-keepclassmembers class com.rewardclub.app.api.** {
    public *;
}

# ─── Jetpack Compose ─────────────────────────────────────────────────────────
# Compose handles its own proguard rules; keeping everything prevents icon shrinking.
-dontwarn androidx.compose.**

# ─── Kotlin Serialization ────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ─── JavaMail / SMTP ─────────────────────────────────────────────────────────
-keep class com.sun.mail.** { *; }
-keep class javax.mail.** { *; }
-dontwarn com.sun.mail.**
-dontwarn javax.mail.**

# ─── Google Play Services / OAuth ────────────────────────────────────────────
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ─── Coil Image Loading ──────────────────────────────────────────────────────
-keep class coil.** { *; }
-dontwarn coil.**

# ─── Cuelinks SDK ────────────────────────────────────────────────────────────
-keep class com.cuelinks.** { *; }
-dontwarn com.cuelinks.**

# ─── Remove all debug logging in release ─────────────────────────────────────
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# ─── Stack trace protection ──────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
