package im.bnw.android.presentation.util

import im.bnw.android.R
import java.io.IOException
import javax.net.ssl.SSLException

class AuthFailedException : IOException()
data class BnwApiError(val description: String) : Throwable()

fun failureMessage(throwable: Throwable) = when (throwable) {
    is SSLException -> R.string.possibly_domain_blocked
    else -> R.string.check_connection_and_tap_retry
}
