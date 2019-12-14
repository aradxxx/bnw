package im.bnw.android.data.core.network.connection_provider

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject


class AndroidConnectionProvider @Inject constructor(private val context: Context) :
    ConnectionProvider {
    override fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
