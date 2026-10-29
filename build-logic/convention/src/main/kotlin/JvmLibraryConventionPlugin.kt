import com.littlestory.buildlogic.configureKotlinJvm
import com.littlestory.buildlogic.enforceDependencyRules
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Pure Kotlin module (no Android): `:core:common`, `:domain`. */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("littlestory.quality")
        configureKotlinJvm()
        enforceDependencyRules()
    }
}
