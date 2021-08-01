package im.bnw.android.presentation.util

import android.content.Context
import im.bnw.android.R
import im.bnw.android.data.core.network.httpresult.HttpException
import java.io.IOException
import javax.net.ssl.SSLException

class AuthFailedException : IOException()
data class BnwApiError(val description: String) : Throwable()
object UserNotFound : Throwable()
object PostNotFound : Throwable()

fun Context.networkFailureMessage(throwable: Throwable?) = when (throwable) {
    is SSLException -> {
        getString(R.string.possibly_domain_blocked)
    }
    is HttpException -> {
        throwable.statusMessage ?: ""
    }
    else -> {
        getString(R.string.check_connection)
    }
}
