@file:Suppress("MagicNumber")

package im.bnw.android.presentation.messages.adapter

import android.graphics.Rect
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.presentation.medialist.MediaAdapter
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.timeAgoString
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.android.synthetic.main.item_message_card.ava
import kotlinx.android.synthetic.main.item_message_card.comments
import kotlinx.android.synthetic.main.item_message_card.date
import kotlinx.android.synthetic.main.item_message_card.id
import kotlinx.android.synthetic.main.item_message_card.recommends
import kotlinx.android.synthetic.main.item_message_card.text
import kotlinx.android.synthetic.main.item_message_card.user
import kotlinx.android.synthetic.main.item_message_card_with_media.*

fun messageDelegate(listener: (Int) -> Unit) =
    adapterDelegateLayoutContainer<MessageItem, MessageListItem>(
        R.layout.item_message_card,
        on = { item, _, _ -> item is MessageItem }
    ) {
        text.movementMethod = LinkMovementMethod.getInstance()
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

        user.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener(position)
            }
        }

        bind {
            val message = item.message
            markwon.setMarkdown(text, message.text)
            user.text = message.user
            date.text = item.message.timestamp().formatDateTime()
            id.text = message.id

            comments.text = message.replyCount.toString()
            recommends.text = message.recommendations.size.toString()

            Glide.with(context)
                .load(String.format(BuildConfig.USER_AVA_URL, message.user))
                .transform(CircleCrop())
                .into(ava)
        }
    }

fun messageWithMediaDelegate(listener: (Int) -> Unit, mediaListener: (Int) -> Unit) =
    adapterDelegateLayoutContainer<MessageWithMediaItem, MessageListItem>(
        R.layout.item_message_card_with_media
    ) {
        text.movementMethod = LinkMovementMethod.getInstance()
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

        val mediaAdapter = MediaAdapter(mediaListener)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        with(media_list) {
            layoutManager = linearLayoutManager.apply { recycleChildrenOnDetach = true }
            adapter = mediaAdapter
            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
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
                }
            )
        }
        user.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener(position)
            }
        }

        bind {
            val message = item.message
            mediaAdapter.items = message.content.media
            markwon.setMarkdown(text, message.text)
            user.text = message.user
            date.text = item.message.timestamp().formatDateTime()
            id.text = message.id
            comments.text = message.replyCount.toString()
            recommends.text = message.recommendations.size.toString()
            Glide.with(context)
                .load(String.format(BuildConfig.USER_AVA_URL, message.user))
                .transform(CircleCrop())
                .into(ava)
        }
    }

val itemCallback: DiffUtil.ItemCallback<MessageListItem> =
    object : DiffUtil.ItemCallback<MessageListItem>() {
        override fun areItemsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
            return when {
                oldItem is MessageItem && newItem is MessageItem -> {
                    oldItem.message.id == newItem.message.id
                }
                oldItem is MessageWithMediaItem && newItem is MessageWithMediaItem -> {
                    oldItem.message.id == newItem.message.id
                }
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: MessageListItem,
            newItem: MessageListItem
        ): Boolean {
            return when {
                oldItem is MessageItem && newItem is MessageItem -> {
                    oldItem.message == newItem.message
                }
                oldItem is MessageWithMediaItem && newItem is MessageWithMediaItem -> {
                    oldItem.message == newItem.message
                }
                else -> false
            }
        }
    }

class MessageAdapter(listener: (Int) -> Unit, mediaListener: (Int) -> Unit) :
    AsyncListDifferDelegationAdapter<MessageListItem>(itemCallback) {
    init {
        delegatesManager.apply {
            addDelegate(messageDelegate(listener))
            addDelegate(messageWithMediaDelegate(listener, mediaListener))
        }
    }
}
