import com.littlestory.buildlogic.quality.CheckTranslationsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class RootConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
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
    }
}
