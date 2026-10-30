import com.android.build.api.variant.HostTestBuilder

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
    testOptions {
        // Compose Preview Screenshot Testing (experimental AGP test suite): renders @PreviewTest
        // functions in src/screenshotTest on the JVM. Lives in :app because only the application
        // packages every library's R classes, which the renderer needs. See
        // docs/testing/screenshot-test-plan.md.
        screenshotTests.create("screenshotTest") {
            engineVersion = libs.versions.composeScreenshot.get()
            targetVariants.add("debug")
            dependencies {
                implementation(project.dependencies.platform(libs.androidx.compose.bom))
                implementation(libs.androidx.compose.ui.tooling)
                implementation(libs.screenshot.validation.api)
            }
        }
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

// The screenshot engine packages resources for every variant through the unit-test components, so
// release needs them enabled even though only debug is screenshot-tested. The release unit-test
// run adds nothing over debug and stays off.
androidComponents {
    beforeVariants(selector().withBuildType("release")) { variant ->
        variant.hostTests[HostTestBuilder.UNIT_TEST_TYPE]?.enable = true
    }
}
tasks.matching { it.name == "testReleaseUnitTest" }.configureEach { enabled = false }
