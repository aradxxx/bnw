package im.bnw.android.presentation.messages.adapter

import android.os.Parcelable
import im.bnw.android.domain.message.Message

interface MessageListItem : Parcelable {
    fun message(): Message
}
