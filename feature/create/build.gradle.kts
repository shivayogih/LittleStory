plugins {
    id("littlestory.android.feature")
}

dependencies {
    // BackHandler: system back on Generating pauses, like the close button.
    implementation(libs.androidx.activity.compose)
}
