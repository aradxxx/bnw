package im.bnw.android.domain.core

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val throwable: Throwable) : Result<Nothing>()
}

fun <T, A> Result<T>.map(transform: (T) -> A): Result<A> {
    return when (this) {
        is Result.Success -> {
            Result.Success(transform(this.value))
        }
        is Result.Failure -> {
            this
        }
    }
}
