plugins {
    `kotlin-dsl`
}

group = "com.littlestory.buildlogic"

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}

dependencies {
    // compileOnly: the root build puts these plugins on the classpath (apply false), so the
    // convention plugins compile against them without pinning a second copy.
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "littlestory.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "littlestory.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "littlestory.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "littlestory.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "littlestory.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = "littlestory.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
