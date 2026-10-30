import com.littlestory.buildlogic.quality.CheckTranslationsTask
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

class RootConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("base")
        val localesConfig = file("app/src/main/res/xml/locales_config.xml")
        val checkTranslations = tasks.register<CheckTranslationsTask>("checkTranslations") {
            group = "verification"
            description = "Fails when a language's strings drift from English (missing keys, orphans, mismatched arguments)."
            // Only each module's src tree is an input, so build outputs of other tasks are never read.
            resourceFiles.from(provider {
                subprojects.map { module ->
                    fileTree(module.projectDir.resolve("src")) {
                        include("main/res/values/strings.xml", "debug/res/values/strings.xml")
                    }
                }
            })
            // Tags other than the default come from locales_config.xml, which a test keeps equal to the registry.
            tags.set(provider {
                Regex("""android:name="([^"]+)"""").findAll(localesConfig.readText())
                    .map { it.groupValues[1] }.filter { it != "en" }.toList()
            })
        }
        allprojects {
            tasks.matching { it.name == "check" }.configureEach { dependsOn(checkTranslations) }
        }
        configureCoverageGate()
    }

    /**
     * LS-120: aggregates every module's coverage into one report and fails `check` when the
     * gated code (view models and domain logic) drops below [MIN_LINE_COVERAGE] percent of lines.
     */
    private fun Project.configureCoverageGate() {
        pluginManager.apply("org.jetbrains.kotlinx.kover")
        // Folder-only parents such as :core and :feature have no build file and nothing to measure.
        subprojects.filter { it.buildFile.exists() }.forEach { module -> dependencies.add("kover", module) }
        extensions.configure<KoverProjectExtension> {
            reports {
                filters {
                    includes { classes(GATED_CLASSES) }
                    excludes { classes(UNGATED_CLASSES) }
                }
                verify {
                    rule("View models and domain logic") { minBound(MIN_LINE_COVERAGE) }
                }
            }
        }
        tasks.named("check") { dependsOn("koverVerify", "koverHtmlReport", "koverXmlReport") }
    }

    private companion object {
        const val MIN_LINE_COVERAGE = 80
        val GATED_CLASSES = listOf("*ViewModel", "*ViewModel$*", "com.littlestory.domain.*")

        // Hilt-generated factories and modules carry no logic of ours.
        val UNGATED_CLASSES = listOf("*_Factory", "*_Factory$*", "*_HiltModules*", "hilt_aggregated_deps.*")
    }
}
