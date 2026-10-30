plugins {
    id("littlestory.android.library")
    id("littlestory.android.hilt")
    id("littlestory.android.room")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
}
