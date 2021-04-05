package im.bnw.android.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import im.bnw.android.domain.usermanager.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : UserDataStore {
    private object PreferencesKeys {
        val USER_NAME = preferencesKey<String>("name")
        val USER_TOKEN = preferencesKey<String>("token")
    }

    override suspend fun updateUserToken(token: String) {
        dataStore.edit {
            it[PreferencesKeys.USER_TOKEN] = token
        }
    }

    override fun subscribeUserToken(): Flow<String> {
        return dataStore.data.map {
            it[PreferencesKeys.USER_TOKEN].orEmpty()
        }
    }

    override suspend fun updateUserName(userName: String) {
        dataStore.edit {
            it[PreferencesKeys.USER_NAME] = userName
        }
    }

    override fun subscribeUserName(): Flow<String> {
        return dataStore.data.map {
            it[PreferencesKeys.USER_NAME].orEmpty()
        }
    }
}
