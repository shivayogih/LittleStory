package com.littlestory.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/** The `libs` version catalog (gradle/libs.versions.toml), the only home for versions. */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.version(alias: String): String = findVersion(alias).get().requiredVersion

internal fun VersionCatalog.intVersion(alias: String): Int = version(alias).toInt()

internal fun VersionCatalog.library(alias: String) = findLibrary(alias).get()

/** Base package for every module; the namespace is derived from the Gradle path, e.g. `:feature:home`. */
internal const val BASE_NAMESPACE = "com.littlestory"

internal val Project.derivedNamespace: String
    get() = BASE_NAMESPACE + path.replace(':', '.').replace('-', '_')
