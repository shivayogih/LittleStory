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
