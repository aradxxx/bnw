package im.bnw.android.presentation.messages.adapter

import android.graphics.Rect
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.presentation.media_list.MediaAdapter
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.timeAgoString
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.android.synthetic.main.item_message_card.ava
import kotlinx.android.synthetic.main.item_message_card.comments
import kotlinx.android.synthetic.main.item_message_card.comments_icon
import kotlinx.android.synthetic.main.item_message_card.date
import kotlinx.android.synthetic.main.item_message_card.id
import kotlinx.android.synthetic.main.item_message_card.recommends
import kotlinx.android.synthetic.main.item_message_card.recommends_icon
import kotlinx.android.synthetic.main.item_message_card.text
import kotlinx.android.synthetic.main.item_message_card.user
import kotlinx.android.synthetic.main.item_message_card_with_media.*

val messageDelegate = adapterDelegateLayoutContainer<MessageItem, MessageListItem>(
    R.layout.item_message_card,
    on = { item, items, position -> item is MessageItem }
) {
    text.movementMethod = LinkMovementMethod.getInstance()
    val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
    val colorSecondaryLight = ContextCompat.getColor(context, R.color.text_secondary_dark)
    val markwon = Markwon.builder(context)
        .usePlugin(LinkifyPlugin.create())
        .build()

    date.setOnLongClickListener {
        Toast.makeText(
            context,
            timeAgoString(context, item.message.timestamp()),
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    bind {
        val message = item.message
        markwon.setMarkdown(text, message.text)
        user.text = message.user
        date.text = formatDateTime(item.message.timestamp())
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
        R.layout.item_message_card_with_media
    ) {
        text.movementMethod = LinkMovementMethod.getInstance()
        val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
        val colorSecondaryLight = ContextCompat.getColor(context, R.color.text_secondary_dark)
        val markwon = Markwon.builder(context)
            .usePlugin(LinkifyPlugin.create())
            .build()

        date.setOnLongClickListener {
            Toast.makeText(
                context,
                timeAgoString(context, item.message.timestamp()),
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        val mediaAdapter = MediaAdapter()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        with(media_list) {
            layoutManager = linearLayoutManager
            adapter = mediaAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                val normal = 16.dpToPx
                val half = 8.dpToPx
                override fun getItemOffsets(
                    outRect: Rect,
                    itemPosition: Int,
                    parent: RecyclerView
                ) {
                    if (itemPosition == 0) {
                        outRect.left = normal
                    } else {
                        outRect.left = half
                    }
                    outRect.bottom = half
                    outRect.top = half
                    if (itemPosition + 1 == parent.adapter?.itemCount) {
                        outRect.right = normal
                    } else {
                        outRect.right = 0
                    }
                }
            })
        }

        bind {
            val message = item.message

            markwon.setMarkdown(text, message.text)
            user.text = message.user

            date.text = formatDateTime(item.message.timestamp())
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

            mediaAdapter.items = message.content.media
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
