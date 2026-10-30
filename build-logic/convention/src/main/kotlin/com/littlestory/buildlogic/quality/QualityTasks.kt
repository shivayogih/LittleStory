package com.littlestory.buildlogic.quality

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/** Fails the build when a module's Kotlin sources contain hardcoded colours, dimensions or text. */
abstract class CheckNoHardcodingTask : DefaultTask() {
    @get:InputFiles
    @get:SkipWhenEmpty
    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    @TaskAction
    fun check() {
        val problems = HardcodingScanner.scan(sources.asFileTree.files.asSequence().filter { it.extension == "kt" })
        if (problems.isNotEmpty()) {
            throw GradleException("Hardcoded values found (see CLAUDE.md no-hardcoding policy):\n" + problems.joinToString("\n"))
        }
    }
}

/** Fails the build when a language's strings drift from English (missing keys, orphans, mismatched %1$s). */
abstract class CheckTranslationsTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val resourceFiles: ConfigurableFileCollection

    /** Language tags other than the default, taken from the locales_config.xml that mirrors the registry. */
    @get:Input
    abstract val tags: ListProperty<String>

    @TaskAction
    fun check() {
        val resDirs = resourceFiles.files.map { it.parentFile.parentFile }.distinct()
        val problems = resDirs.flatMap { TranslationChecker.check(it, tags.get()) }
        if (problems.isNotEmpty()) {
            throw GradleException("Translation check failed:\n" + problems.joinToString("\n"))
        }
    }
}
