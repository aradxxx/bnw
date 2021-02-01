package im.bnw.android.domain.core

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val throwable: Throwable? = null) : Result<Nothing>()
}
