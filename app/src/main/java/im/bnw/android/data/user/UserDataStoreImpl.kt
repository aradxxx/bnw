package im.bnw.android.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import im.bnw.android.domain.usermanager.UserDataStore
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserDataStore {
    companion object {
        private val USER_NAME = stringPreferencesKey("name")
        private val USER_TOKEN = stringPreferencesKey("token")
        private val POST_DRAFT = stringPreferencesKey("postDraft")
    }

    override suspend fun updateUserToken(token: String) {
        dataStore.edit {
            it[USER_TOKEN] = token
        }
    }

    override fun subscribeUserToken() = dataStore.data.map {
        it[USER_TOKEN].orEmpty()
    }

    override suspend fun updateUserName(userName: String) {
        dataStore.edit {
            it[USER_NAME] = userName
        }
    }

    override fun subscribeUserName() = dataStore.data.map {
        it[USER_NAME].orEmpty()
    }

    override suspend fun updatePostDraft(text: String) {
        dataStore.edit {
            it[POST_DRAFT] = text
        }
    }

    override fun subscribePostDraft() = dataStore.data.map {
        it[POST_DRAFT].orEmpty()
    }
}
