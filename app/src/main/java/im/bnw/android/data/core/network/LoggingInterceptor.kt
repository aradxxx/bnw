package im.bnw.android.data.core.network

import im.bnw.android.domain.usermanager.UserDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

private const val QUERY_PARAMETER_LOGIN = "login"

class LoggingInterceptor @Inject constructor(private val userDataStore: UserDataStore) : Interceptor {
    @Suppress("TooGenericExceptionCaught")
    private val token: String
        get() {
            return try {
                runBlocking {
                    userDataStore.subscribeUserToken().first()
                }
            } catch (t: Throwable) {
                Timber.e(t)
                ""
            }
        }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (token.isEmpty()) {
            return chain.proceed(chain.request())
        }
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .addQueryParameter(QUERY_PARAMETER_LOGIN, token)
            .build()
        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}
