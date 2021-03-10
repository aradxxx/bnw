package im.bnw.android.data.core.network.httpresult

class HttpException(
    val statusCode: Int = -1,
    val statusMessage: String? = null,
    val url: String? = null,
    cause: Throwable? = null
) : Exception(null, cause)
