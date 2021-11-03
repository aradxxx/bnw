package im.bnw.android.presentation.util

import android.content.Context
import im.bnw.android.R
import im.bnw.android.data.core.network.httpresult.HttpException
import im.bnw.android.presentation.core.BnwApiErrorEvent
import im.bnw.android.presentation.core.DialogEvent
import im.bnw.android.presentation.core.Event
import java.io.IOException
import javax.net.ssl.SSLException

data class BnwApiError(
    val description: String
) : Throwable()

object AuthFailedException : IOException()
object UserNotFoundException : Throwable()
object PostNotFoundException : Throwable()
object EmptyDraftException : Throwable()

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

fun Throwable.toEvent(): Event? {
    return when (this) {
        is SSLException -> {
            DialogEvent(
                R.string.no_connection,
                R.string.possibly_domain_blocked
            )
        }
        is HttpException,
        is IOException -> {
            DialogEvent(
                R.string.no_connection,
                R.string.check_connection
            )
        }
        is BnwApiError -> {
            BnwApiErrorEvent(description)
        }
        else -> null
    }
}
