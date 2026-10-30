import com.littlestory.buildlogic.quality.CheckNoHardcodingTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/** Static analysis for every module: Detekt, ktlint and, in UI modules, the no-hardcoding check (ADR-018). */
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("io.gitlab.arturbosch.detekt")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")
        // Coverage is measured per module and aggregated and gated at the root (LS-120).
        pluginManager.apply("org.jetbrains.kotlinx.kover")

        extensions.configure<DetektExtension> {
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))
            buildUponDefaultConfig = true
            // No baseline file: existing code must be clean (LS-034 acceptance criterion 2).
        }
        extensions.configure<KtlintExtension> {
            android.set(true)
        }

        if (isHardcodingCheckedModule(path)) {
            val checkNoHardcoding = tasks.register<CheckNoHardcodingTask>("checkNoHardcoding") {
                group = "verification"
                description = "Fails if Kotlin sources contain hardcoded colours, dimensions or user-facing text."
                sources.from(fileTree("src") { include("**/*.kt"); exclude("test/**", "androidTest/**") })
            }
            tasks.matching { it.name == "check" }.configureEach { dependsOn(checkNoHardcoding) }
        }
    }

    // :core:designsystem owns the raw values; :domain/:data/:core:common hold no UI.
    private fun isHardcodingCheckedModule(path: String) =
        path == ":app" || path == ":core:ui" || path.startsWith(":feature:")
}
