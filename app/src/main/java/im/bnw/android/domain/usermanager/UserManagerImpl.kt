package im.bnw.android.domain.usermanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.httpresult.toResult
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.profile.User
import im.bnw.android.presentation.util.AuthFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val api: Api,
    private val dataStore: DataStore<Preferences>,
    private val dispatchersProvider: DispatchersProvider
) : UserManager {
    override suspend fun login(userName: String, password: String) = withContext(dispatchersProvider.io) {
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

    override suspend fun logout(): Unit = withContext(dispatchersProvider.io) {
        dataStore.edit {
            it[PreferencesKeys.USER_TOKEN] = ""
            it[PreferencesKeys.USER_NAME] = ""
        }
    }

    override fun userInfo(): Flow<Result<User?>> {
        return getUserName()
            .map {
                if (it.isEmpty()) {
                    return@map Result.Success(null)
                }
                return@map api.userInfo(it).toResult { response ->
                    User(
                        response.user,
                        response.messagesCount,
                        response.regDate,
                        response.commentsCount
                    )
                }
            }
            .catch {
                emit(Result.Failure(it))
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

    override fun subscribeToken(): Flow<String> {
        return dataStore.data.map {
            it[PreferencesKeys.USER_TOKEN].orEmpty()
        }
    }
}

private object PreferencesKeys {
    val USER_NAME = preferencesKey<String>("name")
    val USER_TOKEN = preferencesKey<String>("token")
}
