import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

// ── Read API keys from local.properties ───────────────────────────────────
val localProps = Properties()
val localFile  = rootProject.file("local.properties")
if (localFile.exists()) localProps.load(localFile.inputStream())

android {
    namespace  = "com.hastashilpa.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hastashilpa.app"
        minSdk        = 25
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"

        // ── Inject API keys as BuildConfig fields ──────────────────────
        buildConfigField(
            "String", "UNSPLASH_KEY",
            "\"${localProps.getProperty("UNSPLASH_ACCESS_KEY", "")}\""
        )
        buildConfigField(
            "String", "GEMINI_KEY",
            "\"${localProps.getProperty("GEMINI_API_KEY", "")}\""
        )
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions { jvmTarget = "11" }

    buildFeatures {
        compose      = true
        buildConfig  = true   // ← required for BuildConfig fields to generate
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.volley)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.compose.material3)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Required for ripple() API used in MainActivity
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.ripple)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Room
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    kapt("androidx.room:room-compiler:2.7.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("androidx.security:security-crypto:1.1.0")

    // Coil — single entry, no duplicate
    implementation("io.coil-kt:coil-compose:2.6.0")
}