import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.littlestory.buildlogic.libs
import com.littlestory.buildlogic.library

/** A screen module: Android library + Compose + Hilt + the core modules every feature uses. */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("littlestory.android.library")
        pluginManager.apply("littlestory.android.compose")
        pluginManager.apply("littlestory.android.hilt")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        dependencies {
            add("implementation", project(":core:designsystem"))
            add("implementation", project(":core:ui"))
            add("implementation", project(":core:common"))
            add("implementation", project(":domain"))
            add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
            add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
            add("implementation", libs.library("androidx-navigation-compose"))
            add("implementation", libs.library("androidx-hilt-navigation-compose"))
            add("implementation", libs.library("kotlinx-serialization-json"))
        }
    }
}
