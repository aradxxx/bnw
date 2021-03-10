@file:Suppress("unused")

package im.bnw.android.data.core.network.httpresult

import im.bnw.android.domain.core.Result
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun <T> HttpResult<T>.isSuccess(): Boolean {
    return this is HttpResult.Success
}

fun <T> HttpResult<T>.asSuccess(): HttpResult.Success<T> {
    return this as HttpResult.Success<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> HttpResult<T>.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure is HttpResult.Failure<*>)
    }
    return this is HttpResult.Failure<*>
}

fun <T> HttpResult<T>.asFailure(): HttpResult.Failure<*> {
    return this as HttpResult.Failure<*>
}

fun <T, R> HttpResult<T>.map(transform: (value: T) -> R): HttpResult<R> {
    return when (this) {
        is HttpResult.Success -> HttpResult.Success.Value(transform(value))
        is HttpResult.Failure<*> -> this
    }
}

fun <T, R> HttpResult<T>.flatMap(transform: (result: HttpResult<T>) -> HttpResult<R>): HttpResult<R> {
    return transform(this)
}

fun <T> HttpResult<T>.toResult(): Result<T> {
    return when (this) {
        is HttpResult.Success -> Result.Success(asSuccess().value)
        else -> Result.Failure(asFailure().error ?: HttpException())
    }
}

fun <T, R> HttpResult<T>.toResult(mapper: (value: T) -> R): Result<R> {
    return when (this) {
        is HttpResult.Success -> Result.Success(mapper(asSuccess().value))
        else -> Result.Failure(asFailure().error ?: HttpException())
    }
}
