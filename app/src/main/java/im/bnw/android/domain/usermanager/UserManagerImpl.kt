package im.bnw.android.domain.usermanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import im.bnw.android.data.core.network.Api
import im.bnw.android.presentation.util.AuthFailedException
import im.bnw.android.presentation.util.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val api: Api,
    private val dataStore: DataStore<Preferences>
) : UserManager {
    override suspend fun login(userName: String, password: String) =
        withContext(Dispatchers.IO) {
            val response = api.getLogin(userName, password)
            if (response.ok) {
                dataStore.setValue(PreferencesKeys.USER_TOKEN, response.token)
            } else {
                throw AuthFailedException()
            }
        }

    override suspend fun logout() {
        dataStore.setValue(PreferencesKeys.USER_TOKEN, "")
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[PreferencesKeys.USER_TOKEN]?.isNotEmpty() ?: false
        }
    }
}

private object PreferencesKeys {
    val USER_TOKEN = preferencesKey<String>("token")
}
