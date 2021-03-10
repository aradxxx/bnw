package im.bnw.android.data.core.network.httpresult

interface HttpResponse {
    val statusCode: Int
    val statusMessage: String?
    val url: String?
}
