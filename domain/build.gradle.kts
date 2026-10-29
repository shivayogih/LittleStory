plugins {
    id("littlestory.jvm.library")
}

dependencies {
    api(projects.core.common)
    api(libs.kotlinx.coroutines.core) // repository interfaces expose Flow
}
