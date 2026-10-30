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
import com.littlestory.data.prefs.ActiveProfileStore
import com.littlestory.data.prefs.PreferencesRepositoryImpl
import com.littlestory.data.repository.DownloadsRepositoryImpl
import com.littlestory.data.repository.FavouritesRepositoryImpl
import com.littlestory.data.repository.ProfilesRepositoryImpl
import com.littlestory.data.repository.ProgressRepositoryImpl
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.FavouritesRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.ProgressRepository
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
    fun catalog(
        @ApplicationContext context: Context,
    ): CatalogRepository =
        CatalogRepositoryImpl(
            source =
                MockContentSource { fileName ->
                    runCatching {
                        context.assets
                            .open(
                                "$MOCK_ASSET_DIR/$fileName",
                            ).bufferedReader()
                            .use { it.readText() }
                    }.getOrNull()
                },
            dispatcher = Dispatchers.IO,
        )
}
