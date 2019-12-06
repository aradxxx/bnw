package im.bnw.android.presentation.messages.adapter

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.timeAgoString
import kotlinx.android.synthetic.main.item_message_card.*

val messageDelegate = adapterDelegateLayoutContainer<MessageItem, MessageListItem>(
    R.layout.item_message_card,
    on = { item, items, position -> item is MessageItem }
) {
    text.movementMethod = LinkMovementMethod.getInstance()
    val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
    val colorSecondaryLight = ContextCompat.getColor(context, R.color.text_secondary_dark)

    date.setOnLongClickListener {
        Toast.makeText(
            context,
            formatDateTime(item.message.timestamp()),
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    bind {
        val message = item.message

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text.text = Html.fromHtml(message.content.text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            text.text = Html.fromHtml(message.content.text)
        }

        user.text = message.user

        date.text = timeAgoString(context, message.timestamp())

        id.text = message.id

        comments.text = message.replyCount.toString()
        if (message.replyCount == 0) {
            comments.setTextColor(colorSecondaryLight)
            comments_icon.setColorFilter(
                colorSecondaryLight,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            comments.setTextColor(colorAccent)
            comments_icon.setColorFilter(colorAccent, android.graphics.PorterDuff.Mode.SRC_IN)
        }

        recommends.text = message.recommendations.size.toString()
        if (message.recommendations.isEmpty()) {
            recommends.setTextColor(colorSecondaryLight)
            recommends_icon.setColorFilter(
                colorSecondaryLight,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            recommends.setTextColor(colorAccent)
            recommends_icon.setColorFilter(colorAccent, android.graphics.PorterDuff.Mode.SRC_IN)
        }

        Glide.with(context)
            .load(String.format(BuildConfig.USER_AVA_URL, message.user))
            .transform(CircleCrop())
            .into(ava)
    }
}

val messageWithMediaDelegate =
    adapterDelegateLayoutContainer<MessageWithMediaItem, MessageListItem>(
        R.layout.item_message_card
    ) {
        text.movementMethod = LinkMovementMethod.getInstance()
        val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
        val colorSecondaryLight = ContextCompat.getColor(context, R.color.text_secondary_light)
        date.setOnLongClickListener {
            Toast.makeText(
                context,
                formatDateTime(item.message.timestamp()),
                Toast.LENGTH_SHORT
            ).show()
            true
        }
        bind {
            val message = item.message

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                text.text = Html.fromHtml(message.content.text, Html.FROM_HTML_MODE_COMPACT)
            } else {
                text.text = Html.fromHtml(message.content.text)
            }
            user.text = message.user

            date.text = timeAgoString(context, message.timestamp())
            id.text = message.id

            comments.text = message.replyCount.toString()
            if (message.replyCount == 0) {
                comments.setTextColor(colorSecondaryLight)
                comments_icon.setColorFilter(
                    colorSecondaryLight,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                comments.setTextColor(colorAccent)
                comments_icon.setColorFilter(colorAccent, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            recommends.text = message.recommendations.size.toString()
            if (message.recommendations.isEmpty()) {
                recommends.setTextColor(colorSecondaryLight)
                recommends_icon.setColorFilter(
                    colorSecondaryLight,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                recommends.setTextColor(colorAccent)
                recommends_icon.setColorFilter(colorAccent, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            Glide.with(context)
                .load(String.format(BuildConfig.USER_AVA_URL, message.user))
                .transform(CircleCrop())
                .into(ava)
        }
    }

val itemCallback: DiffUtil.ItemCallback<MessageListItem> =
    object : DiffUtil.ItemCallback<MessageListItem>() {
        override fun areItemsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
            if (oldItem is MessageItem && newItem is MessageItem) {
                return oldItem.message.id == newItem.message.id
            }
            if (oldItem is MessageWithMediaItem && newItem is MessageWithMediaItem) {
                return oldItem.message.id == newItem.message.id
            }
            return false
        }

        override fun areContentsTheSame(
            oldItem: MessageListItem,
            newItem: MessageListItem
        ): Boolean {
            if (oldItem is MessageItem && newItem is MessageItem) {
                return oldItem.message == newItem.message
            }
            if (oldItem is MessageWithMediaItem && newItem is MessageWithMediaItem) {
                return oldItem.message == newItem.message
            }
            return false
        }
    }

class MessageAdapter : AsyncListDifferDelegationAdapter<MessageListItem>(itemCallback) {
    init {
        delegatesManager.apply {
            addDelegate(messageDelegate)
            addDelegate(messageWithMediaDelegate)
        }
    }
}
