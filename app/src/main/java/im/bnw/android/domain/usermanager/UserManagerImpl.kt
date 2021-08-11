package im.bnw.android.domain.usermanager

import com.squareup.moshi.JsonDataException
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.httpresult.asFailure
import im.bnw.android.data.core.network.httpresult.isFailure
import im.bnw.android.data.core.network.httpresult.toResult
import im.bnw.android.domain.core.Result
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.settings.SettingsInteractor
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.util.AuthFailedException
import im.bnw.android.presentation.util.EmptyDraftException
import im.bnw.android.presentation.util.UserNotFoundException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserManagerImpl @Inject constructor(
    private val api: Api,
    private val userDataStore: UserDataStore,
    private val settings: SettingsInteractor,
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
                throw AuthFailedException
            }
        }

    override suspend fun logout() = withContext(dispatchersProvider.io) {
        userDataStore.updateUserToken("")
        userDataStore.updateUserName("")
    }

    override suspend fun getUserInfo(userName: String) = withContext(dispatchersProvider.io) {
        val result = api.userInfo(userName)
        if (result.isFailure() && result.asFailure().error is JsonDataException) {
            Result.Failure(UserNotFoundException)
        } else {
            result.toResult { response ->
                User(
                    response.user,
                    response.messagesCount,
                    response.regDate,
                    response.commentsCount,
                )
            }
        }
    }

    override fun userInfo() = getUserName()
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

    override fun isAuthenticated() = userDataStore.subscribeUserToken()
        .map {
            it.isNotEmpty()
        }

    override fun getUserName() = userDataStore.subscribeUserName()

    override fun subscribeToken() = userDataStore.subscribeUserToken()

    override suspend fun saveDraft(text: String) = withContext(dispatchersProvider.io) {
        if (!settings.subscribeSettings().first().savePostDraft) {
            return@withContext
        }
        userDataStore.updatePostDraft(text)
    }

    override suspend fun deleteDraft() {
        userDataStore.updatePostDraft("")
    }

    override suspend fun draft(): Result<String> = withContext(dispatchersProvider.io) {
        if (isAuthenticated().first() && settings.subscribeSettings().first().savePostDraft) {
            Result.Success(userDataStore.subscribePostDraft().first())
        } else {
            Result.Failure(EmptyDraftException)
        }
    }
}
