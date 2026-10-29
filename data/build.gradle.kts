plugins {
    id("littlestory.android.library")
    id("littlestory.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
