package com.dicoding.submission.storyapp.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                token = preferences[TOKEN_KEY] ?: "",
                isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }
}

data class User(
    val token: String,
    val isLoggedIn: Boolean
)
