package im.bnw.android.presentation.messages

interface MessageClickListener {
    fun cardClicked(position: Int)
    fun userClicked(position: Int)
    fun mediaClicked(position: Int, mediaPosition: Int)
    fun saveMessageClicked(position: Int)
    fun saveReplyClicked(position: Int)
    fun replyCardClicked(position: Int)
    fun quoteClicked(position: Int)
}
