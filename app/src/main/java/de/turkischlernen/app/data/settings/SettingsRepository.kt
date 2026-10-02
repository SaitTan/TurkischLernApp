package de.turkischlernen.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "einstellungen")

data class AppSettings(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    /** Aussehen und Name des Maskottchens. */
    val avatar: AvatarConfig = AvatarConfig()
)

/**
 * Geräte-Einstellungen aus dem Eltern-Bereich. Bewusst getrennt vom
 * Lernfortschritt, damit "Fortschritt zurücksetzen" sie nicht löscht.
 */
class SettingsRepository(context: Context) {

    private val store = context.applicationContext.settingsStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object Keys {
        val sound = booleanPreferencesKey("sound_enabled")
        val haptics = booleanPreferencesKey("haptics_enabled")
        val avatarFur = stringPreferencesKey("avatar_fur")
        val avatarAccessory = stringPreferencesKey("avatar_accessory")
        val avatarName = stringPreferencesKey("avatar_name")
    }

    /** Synchron lesbar, damit Sounds und Vibration ohne Verzögerung prüfen können. */
    val current: StateFlow<AppSettings> = store.data
        .map { prefs ->
            AppSettings(
                soundEnabled = prefs[Keys.sound] ?: true,
                hapticsEnabled = prefs[Keys.haptics] ?: true,
                avatar = AvatarConfig(
                    furId = prefs[Keys.avatarFur] ?: AvatarOptions.DEFAULT_FUR,
                    accessoryId = prefs[Keys.avatarAccessory] ?: AvatarOptions.DEFAULT_ACCESSORY,
                    name = prefs[Keys.avatarName] ?: AvatarOptions.DEFAULT_NAME
                )
            )
        }
        .catch { emit(AppSettings()) }
        .stateIn(scope, SharingStarted.Eagerly, AppSettings())

    suspend fun setSoundEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.sound] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.haptics] = enabled }
    }

    suspend fun setAvatarFur(id: String) {
        store.edit { prefs -> prefs[Keys.avatarFur] = id }
    }

    suspend fun setAvatarAccessory(id: String) {
        store.edit { prefs -> prefs[Keys.avatarAccessory] = id }
    }

    /** Name des Hundes – zu lange Eingaben werden gekürzt. */
    suspend fun setAvatarName(name: String) {
        store.edit { prefs -> prefs[Keys.avatarName] = AvatarOptions.cleanName(name) }
    }
}
