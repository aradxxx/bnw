package im.bnw.android.data.core.network

import im.bnw.android.data.core.network.connection_provider.ConnectionProvider
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ConnectionInterceptor @Inject constructor(private val connectionProvider: ConnectionProvider) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (!connectionProvider.isInternetAvailable()) {
            throw IOException()
        }
        return chain.proceed(request)
    }
}
