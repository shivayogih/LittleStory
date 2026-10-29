plugins {
    id("littlestory.android.application")
    id("littlestory.android.compose")
    id("littlestory.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        applicationId = "com.littlestory.app"
        versionCode = 1
        versionName = "0.1.0"
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.home)
    implementation(projects.feature.discover)
    implementation(projects.feature.create)
    implementation(projects.feature.reader)
    implementation(projects.feature.library)
    implementation(projects.feature.settings)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}

