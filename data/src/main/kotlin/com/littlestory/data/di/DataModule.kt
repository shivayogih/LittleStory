package com.littlestory.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.littlestory.core.common.config.AppConfig
import com.littlestory.data.catalog.CatalogRepositoryImpl
import com.littlestory.data.catalog.MockContentSource
import com.littlestory.data.db.LittleStoryDatabase
import com.littlestory.data.generation.GenerationRepositoryImpl
import com.littlestory.data.library.LibraryRepositoryImpl
import com.littlestory.data.prefs.ActiveProfileStore
import com.littlestory.data.prefs.PreferencesRepositoryImpl
import com.littlestory.data.repository.DownloadsRepositoryImpl
import com.littlestory.data.repository.FavouritesRepositoryImpl
import com.littlestory.data.repository.LocalDataRepositoryImpl
import com.littlestory.data.repository.ProfilesRepositoryImpl
import com.littlestory.data.repository.ProgressRepositoryImpl
import com.littlestory.data.settings.NotificationsRepositoryImpl
import com.littlestory.data.settings.SubscriptionRepositoryImpl
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.GenerationRepository
import com.littlestory.domain.repository.LibraryRepository
import com.littlestory.domain.repository.LocalDataRepository
import com.littlestory.domain.repository.NotificationsRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
import com.littlestory.domain.repository.StoryStores
import com.littlestory.domain.repository.SubscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.UUID
import javax.inject.Singleton

private const val DATASTORE_FILE = "littlestory_prefs.preferences_pb"
private const val MOCK_ASSET_DIR = "mock"

/** Wires the local persistence layer (ADR-004). Only `:app` sees this module. */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun dataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { File(context.filesDir, "datastore/$DATASTORE_FILE") }

    @Provides
    @Singleton
    fun database(
        @ApplicationContext context: Context,
    ): LittleStoryDatabase =
        Room.databaseBuilder(context, LittleStoryDatabase::class.java, LittleStoryDatabase.NAME).build()

    @Provides
    @Singleton
    fun preferences(store: DataStore<Preferences>): PreferencesRepository = PreferencesRepositoryImpl(store)

    @Provides
    @Singleton
    fun activeProfileStore(store: DataStore<Preferences>) = ActiveProfileStore(store)

    @Provides
    @Singleton
    fun profiles(db: LittleStoryDatabase, active: ActiveProfileStore): ProfilesRepository =
        ProfilesRepositoryImpl(
            dao = db.childProfileDao(),
            savedDao = db.savedStoryDao(),
            progressDao = db.readingProgressDao(),
            generatedDao = db.generatedStoryDao(),
            active = active,
            newId = { UUID.randomUUID().toString() },
        )

    @Provides
    @Singleton
    fun favourites(db: LittleStoryDatabase): FavouritesRepository = FavouritesRepositoryImpl(db.savedStoryDao())

    @Provides
    @Singleton
    fun progress(db: LittleStoryDatabase): ProgressRepository = ProgressRepositoryImpl(db.readingProgressDao())

    @Provides
    @Singleton
    fun downloads(db: LittleStoryDatabase): DownloadsRepository =
        DownloadsRepositoryImpl(db.downloadDao()) { AppConfig.Downloads.SIMULATED_STORY_BYTES }

    @Provides
    @Singleton
    fun mockContent(
        @ApplicationContext context: Context,
    ): MockContentSource =
        MockContentSource { fileName ->
            runCatching {
                context.assets
                    .open("$MOCK_ASSET_DIR/$fileName")
                    .bufferedReader()
                    .use { it.readText() }
            }.getOrNull()
        }

    @Provides
    @Singleton
    fun catalog(source: MockContentSource): CatalogRepository = CatalogRepositoryImpl(source, Dispatchers.IO)

    @Provides
    @Singleton
    fun library(db: LittleStoryDatabase): LibraryRepository =
        LibraryRepositoryImpl(db.generatedStoryDao(), System::currentTimeMillis)

    @Provides
    @Singleton
    fun generation(source: MockContentSource, library: LibraryRepository): GenerationRepository =
        GenerationRepositoryImpl(source, library)

    @Provides
    @Singleton
    fun notifications(source: MockContentSource, db: LittleStoryDatabase): NotificationsRepository =
        NotificationsRepositoryImpl(source, db.notificationReadDao(), Dispatchers.IO)

    @Provides
    @Singleton
    fun subscription(source: MockContentSource): SubscriptionRepository =
        SubscriptionRepositoryImpl(source, Dispatchers.IO)

    @Provides
    fun storyStores(
        catalog: CatalogRepository,
        library: LibraryRepository,
        favourites: FavouritesRepository,
        downloads: DownloadsRepository,
        progress: ProgressRepository,
    ) = StoryStores(catalog, library, favourites, downloads, progress)

    @Provides
    @Singleton
    fun localData(db: LittleStoryDatabase, store: DataStore<Preferences>): LocalDataRepository =
        LocalDataRepositoryImpl(db, store, Dispatchers.IO)
}
