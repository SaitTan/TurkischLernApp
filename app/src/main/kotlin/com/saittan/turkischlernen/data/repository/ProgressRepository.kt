package com.saittan.turkischlernen.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")

class ProgressRepository(context: Context) {

    private val store = context.applicationContext.progressDataStore

    val learnedWords: Flow<Set<String>> = store.data.map { prefs ->
        prefs[LEARNED_WORDS] ?: emptySet()
    }

    suspend fun markLearned(categoryId: String, turkish: String) {
        store.edit { prefs ->
            val current = prefs[LEARNED_WORDS] ?: emptySet()
            prefs[LEARNED_WORDS] = current + token(categoryId, turkish)
        }
    }

    companion object {
        private val LEARNED_WORDS = stringSetPreferencesKey("learned_words")
        fun token(categoryId: String, turkish: String): String = "$categoryId:$turkish"
    }
}
