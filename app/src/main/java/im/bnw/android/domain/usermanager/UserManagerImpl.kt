package im.bnw.android.domain.usermanager

import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.httpresult.toResult
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.util.AuthFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val api: Api,
    private val userDataStore: UserDataStore,
    private val dispatchersProvider: DispatchersProvider
) : UserManager {
    override suspend fun login(userName: String, password: String) =
        withContext(dispatchersProvider.io) {
            val response = api.login(userName, password)
            if (response.ok) {
                userDataStore.updateUserName(userName)
                userDataStore.updateUserToken(response.token)
                return@withContext
            } else {
                throw AuthFailedException()
            }
        }

    override suspend fun logout() = withContext(dispatchersProvider.io) {
        userDataStore.updateUserToken("")
        userDataStore.updateUserName("")
    }

    override suspend fun getUserInfo(userName: String): Result<User> =
        withContext(dispatchersProvider.io) {
            api.userInfo(userName).toResult { response ->
                User(
                    response.user,
                    response.messagesCount,
                    response.regDate,
                    response.commentsCount,
                )
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
                        response.commentsCount,
                    )
                }
            }
            .catch {
                emit(Result.Failure(it))
            }
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return userDataStore.subscribeUserToken().map {
            it.isNotEmpty()
        }
    }

    override fun getUserName(): Flow<String> {
        return userDataStore.subscribeUserName()
    }

    override fun subscribeToken(): Flow<String> {
        return userDataStore.subscribeUserToken()
    }
}
