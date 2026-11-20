package fr.formation.popote.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// region datastore
// Délégué d'extension : UNE seule instance de DataStore par fichier
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val LAST_QUERY = stringPreferencesKey("last_query")
    }

    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.REMINDER_ENABLED] ?: false }
    val lastQuery: Flow<String> = context.dataStore.data.map { it[Keys.LAST_QUERY] ?: "" }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REMINDER_ENABLED] = enabled }
    }

    suspend fun setLastQuery(query: String) {
        context.dataStore.edit { it[Keys.LAST_QUERY] = query }
    }
}
// endregion datastore
