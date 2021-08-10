package im.bnw.android.presentation.messages

interface MessageClickListener {
    fun cardClicked(position: Int)
    fun userClicked(position: Int)
    fun mediaClicked(position: Int, mediaPosition: Int)
    fun saveClicked(position: Int)
}
