plugins {
    id("littlestory.jvm.library")
}

dependencies {
    api(projects.core.common)
    implementation(libs.kotlinx.coroutines.core)
}
