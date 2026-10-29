plugins {
    id("littlestory.android.library")
    id("littlestory.android.compose")
}

dependencies {
    api(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.domain)
}
