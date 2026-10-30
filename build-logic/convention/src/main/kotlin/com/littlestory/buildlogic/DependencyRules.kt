package com.littlestory.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

/**
 * Enforces docs/architecture/dependency-rules.md: each module may depend only on the project
 * modules listed for it. `:app` is the composition root and may depend on everything.
 */
internal fun Project.enforceDependencyRules() {
    if (path == APP_PATH) return
    val allowed = allowedProjectDependencies(path)
    configurations.configureEach {
        val configurationName = name
        dependencies.withType(ProjectDependency::class.java).configureEach {
            val target = path
            if (target != this@enforceDependencyRules.path && target !in allowed) {
                throw GradleException(
                    "Dependency rule violation: ${this@enforceDependencyRules.path} -> $target " +
                        "($configurationName). Allowed: ${allowed.ifEmpty { setOf("none") }}. " +
                        "See docs/architecture/dependency-rules.md.",
                )
            }
        }
    }
}

private const val APP_PATH = ":app"
private const val COMMON = ":core:common"
private const val DESIGN_SYSTEM = ":core:designsystem"
private const val CORE_UI = ":core:ui"
private const val DOMAIN = ":domain"

internal fun allowedProjectDependencies(path: String): Set<String> = when {
    path == COMMON -> emptySet()
    path == DESIGN_SYSTEM -> emptySet()
    path == DOMAIN -> setOf(COMMON)
    path == CORE_UI -> setOf(DESIGN_SYSTEM, DOMAIN, COMMON)
    path == ":data" -> setOf(DOMAIN, COMMON)
    path.startsWith(":feature:") -> setOf(DESIGN_SYSTEM, CORE_UI, DOMAIN, COMMON)
    else -> throw GradleException("No dependency rule for $path; add it to DependencyRules.kt")
}
