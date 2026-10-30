package com.littlestory.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The id of the active child, kept in DataStore rather than on the profile row so deleting a
 * child clears it cleanly and switching is one write.
 */
class ActiveProfileStore(private val store: DataStore<Preferences>) {
    val activeProfileId: Flow<String?> = store.data.map { it[KEY] }

    suspend fun set(id: String?) {
        store.edit { if (id == null) it.remove(KEY) else it[KEY] = id }
    }

    private companion object {
        val KEY = stringPreferencesKey("active_profile_id")
    }
}
