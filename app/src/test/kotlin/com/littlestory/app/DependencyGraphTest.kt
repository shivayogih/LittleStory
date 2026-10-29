package com.littlestory.app

import com.littlestory.core.common.config.FeatureFlags
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** The Hilt graph builds and the persistence layer behind it starts empty on a fresh install. */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = HiltTestApplication::class)
class DependencyGraphTest {
    @get:Rule
    val hilt = HiltAndroidRule(this)

    @Inject lateinit var profiles: ProfilesRepository

    @Inject lateinit var preferences: PreferencesRepository

    @Inject lateinit var favourites: FavouritesRepository

    @Inject lateinit var progress: ProgressRepository

    @Inject lateinit var downloads: DownloadsRepository

    @Inject lateinit var flags: FeatureFlags

    @Before
    fun inject() = hilt.inject()

    @Test
    fun freshInstallHasNoProfileAndFirstRunNotCompleted() = runBlocking {
        assertNull(profiles.activeProfile.first())
        assertEquals(false, preferences.firstRunCompleted.first())
        assertEquals(emptySet(), favourites.favouriteIds("none").first())
        assertEquals(emptyList(), progress.progress("none").first())
        assertEquals(emptyList(), downloads.downloads.first())
    }

    @Test
    fun debugBuildsOfferPseudoLocales() {
        assertNotNull(flags)
        assertEquals(BuildConfig.DEBUG, flags.pseudoLocales)
    }
}
