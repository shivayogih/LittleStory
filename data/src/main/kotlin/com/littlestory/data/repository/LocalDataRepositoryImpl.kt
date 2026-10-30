package com.littlestory.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.littlestory.data.db.LittleStoryDatabase
import com.littlestory.domain.repository.LocalDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Wipes every table and every stored preference (LS-114). Room's `clearAllTables` blocks, so it
 * runs on [io]. Afterwards the first-run flag is unset and the app starts at onboarding again.
 */
class LocalDataRepositoryImpl(
    private val db: LittleStoryDatabase,
    private val store: DataStore<Preferences>,
    private val io: CoroutineDispatcher,
) : LocalDataRepository {
    override suspend fun deleteAll() {
        withContext(io) { db.clearAllTables() }
        store.edit { it.clear() }
    }
}
