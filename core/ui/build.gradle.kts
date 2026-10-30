plugins {
    id("littlestory.android.library")
    id("littlestory.android.compose")
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.common)
    implementation(projects.domain)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
}
