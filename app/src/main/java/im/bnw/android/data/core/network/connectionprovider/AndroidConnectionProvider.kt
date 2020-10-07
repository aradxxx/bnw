package im.bnw.android.data.core.network.connectionprovider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import javax.inject.Inject

class AndroidConnectionProvider @Inject constructor(private val context: Context) :
    ConnectionProvider {
    override fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectedNetwork = connectivityManager.allNetworks.firstOrNull { it.isConnected() }
        return connectedNetwork != null
    }

    private fun Network.isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(this)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }
}
