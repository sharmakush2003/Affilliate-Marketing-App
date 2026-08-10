import java.util.Properties

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

val cuelinksApiKey: String = localProperties.getProperty("CUELINKS_API_KEY") ?: ""
val cuelinksChannelId: String = localProperties.getProperty("CUELINKS_CHANNEL_ID") ?: "301603"
val otpServerUrl: String = localProperties.getProperty("OTP_SERVER_URL") ?: ""
val otpApiSecret: String = localProperties.getProperty("OTP_API_SECRET") ?: ""
val supabaseUrl: String = localProperties.getProperty("SUPABASE_URL") ?: ""
val supabaseAnonKey: String = localProperties.getProperty("SUPABASE_ANON_KEY") ?: ""

val releaseStoreFile: String = localProperties.getProperty("RELEASE_STORE_FILE") ?: ""
val releaseStorePassword: String = localProperties.getProperty("RELEASE_STORE_PASSWORD") ?: ""
val releaseKeyAlias: String = localProperties.getProperty("RELEASE_KEY_ALIAS") ?: ""
val releaseKeyPassword: String = localProperties.getProperty("RELEASE_KEY_PASSWORD") ?: ""

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rewardclub.app"
    compileSdk = 36

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    defaultConfig {
        applicationId = "com.rewardclub.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "1.0.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CUELINKS_API_KEY", "\"$cuelinksApiKey\"")
        buildConfigField("String", "CUELINKS_CHANNEL_ID", "\"$cuelinksChannelId\"")
        // ✅ F-01 FIXED: SMTP credentials removed from APK. They live in otp-server/.env only.
        // Android now calls the OTP server via HTTPS instead of doing SMTP directly.
        buildConfigField("String", "OTP_SERVER_URL", "\"$otpServerUrl\"")
        buildConfigField("String", "OTP_API_SECRET", "\"$otpApiSecret\"")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    }

    signingConfigs {
        create("release") {
            if (releaseStoreFile.isNotEmpty()) {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Supabase dependencies
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.ktor:ktor-client-android:3.0.0")

    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
