package im.bnw.android.domain.message

interface MessageInteractor {
    suspend fun messages(after: String, before: String, user: String): List<Message>
    suspend fun post(text: String, anonymous: Boolean)
}
