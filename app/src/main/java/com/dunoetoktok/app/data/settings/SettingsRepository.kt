package com.dunoetoktok.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dunoetoktok.app.model.TextScale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private object SettingsKeys {
    val TEXT_SCALE = stringPreferencesKey("text_scale")
}

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val textScale: Flow<TextScale> = dataStore.data.map { prefs ->
        TextScale.fromName(prefs[SettingsKeys.TEXT_SCALE])
    }

    suspend fun setTextScale(textScale: TextScale) {
        dataStore.edit { it[SettingsKeys.TEXT_SCALE] = textScale.name }
    }
}
