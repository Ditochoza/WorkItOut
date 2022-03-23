package es.ucm.fdi.workitout.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class UserDataStore (val context: Context) {
    companion object {
        //Se pone aquí para que no de error de que hay varios DataStores
        private val Context.dataStore by preferencesDataStore("userPrefs")
    }

    suspend fun putString(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun getStringUserDataStoreOrEmpty(key: String): String {
        val preferenceKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferenceKey] ?: ""
    }

    /*suspend fun deleteUserDataStore() {
        context.dataStore.edit { it.clear() }
    }*/
}