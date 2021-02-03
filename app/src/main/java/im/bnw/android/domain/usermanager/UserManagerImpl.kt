package im.bnw.android.domain.usermanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import im.bnw.android.data.core.network.Api
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.profile.User
import im.bnw.android.presentation.util.AuthFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val api: Api,
    private val dataStore: DataStore<Preferences>
) : UserManager {
    override suspend fun login(userName: String, password: String) = withContext(Dispatchers.IO) {
        val response = api.login(userName, password)
        if (response.ok) {
            dataStore.edit {
                it[PreferencesKeys.USER_TOKEN] = response.token
                it[PreferencesKeys.USER_NAME] = userName
            }
            return@withContext
        } else {
            throw AuthFailedException()
        }
    }

    override suspend fun logout() {
        dataStore.edit {
            it[PreferencesKeys.USER_TOKEN] = ""
            it[PreferencesKeys.USER_NAME] = ""
        }
    }

    override fun userInfo(): Flow<Result<User?>> {
        return getUserName()
            .map {
                if (it.isNotEmpty()) {
                    val response = api.userInfo(it)
                    if (response.ok) {
                        Result.Success(
                            User(
                                response.user,
                                response.messagesCount,
                                response.regDate,
                                response.commentsCount
                            )
                        )
                    } else {
                        Result.Failure(IOException("Response is not ok"))
                    }
                } else {
                    Result.Success(null)
                }
            }
            .catch {
                emit(Result.Failure(IOException("Response is catched")))
            }
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return dataStore.data.map {
            it[PreferencesKeys.USER_TOKEN]?.isNotEmpty() ?: false
        }
    }

    override fun getUserName(): Flow<String> {
        return dataStore.data.map {
            it[PreferencesKeys.USER_NAME].orEmpty()
        }
    }

    override fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[PreferencesKeys.USER_TOKEN].orEmpty()
        }
    }
}

private object PreferencesKeys {
    val USER_NAME = preferencesKey<String>("name")
    val USER_TOKEN = preferencesKey<String>("token")
}
