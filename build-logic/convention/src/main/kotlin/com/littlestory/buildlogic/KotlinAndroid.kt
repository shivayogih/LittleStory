package com.littlestory.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.configureAndroidApplication(extension: ApplicationExtension) {
    extension.apply {
        namespace = derivedNamespace
        compileSdk {
            version = release(libs.intVersion("compileSdk")) {
                minorApiLevel = libs.intVersion("compileSdkMinor")
            }
        }
        defaultConfig {
            minSdk = libs.intVersion("minSdk")
            targetSdk = libs.intVersion("targetSdk")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        testOptions.unitTests.isIncludeAndroidResources = true
    }
    configureKotlinAndroid()
}

internal fun Project.configureAndroidLibrary(extension: LibraryExtension) {
    extension.apply {
        namespace = derivedNamespace
        compileSdk {
            version = release(libs.intVersion("compileSdk")) {
                minorApiLevel = libs.intVersion("compileSdkMinor")
            }
        }
        defaultConfig {
            minSdk = libs.intVersion("minSdk")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        testOptions.unitTests.isIncludeAndroidResources = true
    }
    configureKotlinAndroid()
}

private fun Project.configureKotlinAndroid() {
    extensions.configure<KotlinAndroidProjectExtension> {
        jvmToolchain(libs.intVersion("jdk"))
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(libs.intVersion("jdk"))
    }
}
