package com.littlestory.buildlogic

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/** JVM unit-test stack shared by every module (LS-035): JUnit 4, MockK, coroutines-test, Turbine. */
internal fun Project.addUnitTestDependencies() {
    dependencies {
        add("testImplementation", libs.library("junit4"))
        add("testImplementation", libs.library("kotlin-test"))
        add("testImplementation", libs.library("mockk"))
        add("testImplementation", libs.library("kotlinx-coroutines-test"))
        add("testImplementation", libs.library("turbine"))
    }
}

/** Robolectric + Compose UI test + Roborazzi for Android modules (screenshots run on the JVM, ADR-011). */
internal fun Project.addAndroidUnitTestDependencies() {
    dependencies {
        add("testImplementation", libs.library("robolectric"))
        add("testImplementation", libs.library("androidx-test-ext-junit"))
        add("testImplementation", libs.library("roborazzi"))
        add("testImplementation", libs.library("roborazzi-junit-rule"))
    }
}

internal fun Project.configureTestTasks() {
    tasks.withType<Test>().configureEach {
        useJUnit()
        // Feature modules have no tests until their screens land (P3+).
        failOnNoDiscoveredTests.set(false)
        // Kannada strings in test sources must be read as UTF-8 on every OS.
        systemProperty("file.encoding", "UTF-8")
        // Robolectric reflects into java.io and jdk.internal.access on JDK 17+.
        jvmArgs("--add-opens=java.base/java.io=ALL-UNNAMED", "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
    }
}
