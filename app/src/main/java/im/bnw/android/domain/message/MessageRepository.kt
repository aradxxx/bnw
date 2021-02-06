package im.bnw.android.domain.message

interface MessageRepository {
    suspend fun messages(after: String, before: String, user: String): List<Message>
    suspend fun post(text: String, anonymous: Boolean)
}
