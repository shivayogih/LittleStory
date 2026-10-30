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
    packaging {
        // Prebuilt AndroidX libraries ship already-stripped .so files; keep them as they are
        // instead of logging "Unable to strip the following libraries" on every build.
        jniLibs.keepDebugSymbols += "**/*.so"
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            // en-XA (accented, long) and ar-XB (RTL) test locales, debug only (LS-042).
            isPseudoLocalesEnabled = true
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
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.hilt.testing)
    kspTest(libs.hilt.compiler)
}
