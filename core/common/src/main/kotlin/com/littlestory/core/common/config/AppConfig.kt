package com.littlestory.core.common.config

/**
 * Every configurable value seen in the prototype has exactly one home here
 * (docs/architecture/configuration-and-constants.md). Feature code reads these, never literals.
 */
object AppConfig {
    object Profiles {
        /** Owner of favourites and progress when Setup was skipped and no child profile exists. */
        const val GUEST_ID = "guest"
    }

    object Home {
        /** Categories shown as tiles in "Explore stories", in order. */
        val exploreCategoryIds = listOf("bedtime", "animals", "adventure", "friendship", "indian_tales")
        const val MADE_FOR_COUNT = 4
        const val CULTURE_CATEGORY_ID = "indian_tales"
    }

    object Create {
        const val PROMPT_MAX_CHARS = 500
        val durationOptionsMinutes = listOf(5, 8, 10, 15)
        const val DEFAULT_DURATION_MINUTES = 8
        val themes = listOf("adventure", "bedtime", "animals", "friendship", "fantasy", "learning", "indian_folklore")
        const val DEFAULT_THEME = "adventure"
        val moods = listOf("calm", "funny", "magical", "adventurous")
        const val DEFAULT_MOOD = "magical"
        val suggestionIds = listOf("suggestion_1", "suggestion_2", "suggestion_3", "suggestion_4", "suggestion_5")
    }

    object Setup {
        val ageRange = 3..12
        const val DEFAULT_AGE = 6

        // Differs from Create's options in the prototype (open question C1).
        val durationOptionsMinutes = listOf(5, 10, 15)
        const val DEFAULT_DURATION_MINUTES = 10
        val interestIds = listOf("animals", "cricket", "adventure", "space", "nature", "music")
        val defaultInterestIds = interestIds.take(3)
        const val AVATAR_COUNT = 3
        const val NAME_MAX_CHARS = 30
    }

    object Generation {
        const val STEP_PERCENT = 4
        const val STEP_INTERVAL_MS = 120L
        const val AUTO_ADVANCE_DELAY_MS = 500L
        val stageThresholdsPercent = listOf(38, 76)
        const val RESTART_PERCENT = 8
    }

    object Player {
        const val SKIP_SECONDS = 15
        val speedOptions = listOf(0.75f, 1.0f, 1.25f, 1.5f)
        const val DEFAULT_SPEED = 1.0f
        const val SIMULATED_TICK_MS = 500L
        const val TICK_PERCENT = 0.25f
        const val DEFAULT_VOLUME_PERCENT = 75

        /** Sleep-timer choices in minutes; "end of story" is a separate option in the UI. */
        val sleepTimerMinutes = listOf(10, 15, 30)
    }

    object Reader {
        const val TEXT_SCALE_MIN = 0.9f
        const val TEXT_SCALE_MAX = 1.3f
        const val TEXT_SCALE_STEP = 0.1f
        const val TEXT_SCALE_DEFAULT = 1.0f
    }

    object Downloads {
        /** Simulated storage quota: 1 GB. */
        const val SIMULATED_QUOTA_BYTES = 1_000_000_000L

        /** Size recorded for a simulated download until stories carry their own sizes. */
        const val SIMULATED_STORY_BYTES = 12_000_000L
    }

    object Reminder {
        const val DEFAULT_HOUR = 20
        const val DEFAULT_MINUTE = 30
        val defaultDaysOfWeek = (1..7).toList()
    }

    object Discover {
        val filterIds =
            listOf(
                "all",
                "animals",
                "adventure",
                "friendship",
                "kindness",
                "nature",
                "space",
                "science",
                "panchatantra",
                "indian_folk_tales",
                "festivals",
                "learning",
            )
        val sortOptionIds = listOf("recommended", "shortest", "newest")
        /** Filter id meaning "no category filter"; also the Category route id for the whole catalogue. */
        const val ALL_ID = "all"
        const val DEFAULT_SORT_ID = "recommended"
        const val FEATURED_STORY_ID = "story_little_elephant"
        const val POPULAR_COUNT = 4
        const val BEDTIME_CATEGORY_ID = "bedtime"
    }

    object Ui {
        const val TOAST_DURATION_MS = 2_500L
    }
}
