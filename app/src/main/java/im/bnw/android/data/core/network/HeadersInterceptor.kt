package im.bnw.android.data.core.network

import im.bnw.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

private const val USER_AGENT_HEADER = "User-Agent"
private val userAgentAppName = if (!BuildConfig.DEBUG) {
    "bnw-android"
} else {
    "bnw-android-debug"
}

@Suppress("TooGenericExceptionCaught")
private val httpUserAgent = try {
    System.getProperty("http.agent") ?: ""
} catch (e: Exception) {
    Timber.e(e)
    ""
}
private val userAgentHeaderValue = "$userAgentAppName/${BuildConfig.VERSION_CODE} $httpUserAgent"

object HeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .header(USER_AGENT_HEADER, userAgentHeaderValue)
            .build()
        return chain.proceed(newRequest)
    }
}
