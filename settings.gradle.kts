pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Downloads the JDK the toolchain asks for when it is not installed, so no manual JDK setup.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "LittleStory"

include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:ui")
include(":domain")
include(":data")
include(":feature:onboarding")
include(":feature:home")
include(":feature:discover")
include(":feature:create")
include(":feature:reader")
include(":feature:library")
include(":feature:settings")
include(":prototype:gemini")
