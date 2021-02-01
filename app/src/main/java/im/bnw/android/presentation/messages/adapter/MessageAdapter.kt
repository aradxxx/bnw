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
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.BuildConfig
import im.bnw.android.databinding.ItemMessageCardBinding
import im.bnw.android.databinding.ItemMessageCardWithMediaBinding
import im.bnw.android.presentation.medialist.MediaAdapter
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.timeAgoString
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin

fun messageDelegate(userNameListener: (Int) -> Unit) =
    adapterDelegateViewBinding<MessageItem, MessageListItem, ItemMessageCardBinding>(
        { layoutInflater, root -> ItemMessageCardBinding.inflate(layoutInflater, root, false) }
    ) {
        val markwon = Markwon.builder(context)
            .usePlugin(LinkifyPlugin.create())
            .build()

        fun userClicked() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                userNameListener(position)
            }
        }

        fun showTime() {
            Toast.makeText(
                context.applicationContext,
                timeAgoString(context, item.message.timestamp()),
                Toast.LENGTH_SHORT
            ).show()
        }

        with(binding) {
            text.movementMethod = LinkMovementMethod.getInstance()
            date.setOnClickListener {
                userClicked()
            }
            date.setOnLongClickListener {
                showTime()
                true
            }
            user.setOnClickListener {
                userClicked()
            }
            user.setOnLongClickListener {
                showTime()
                true
            }
        }

        bind {
            val message = item.message
            with(binding) {
                markwon.setMarkdown(text, message.text)

                user.newText = message.user
                date.newText = item.message.timestamp().formatDateTime()
                id.newText = message.id

                comments.newText = message.replyCount.toString()
                recommends.newText = message.recommendations.size.toString()

                Glide.with(context)
                    .load(String.format(BuildConfig.USER_AVA_THUMB_URL, message.user))
                    .transform(CircleCrop())
                    .into(ava)
            }
        }
    }

fun messageWithMediaDelegate(userNameListener: (Int) -> Unit, mediaListener: (Int, Int) -> Unit) =
    adapterDelegateViewBinding<MessageWithMediaItem, MessageListItem, ItemMessageCardWithMediaBinding>(
        { layoutInflater, root -> ItemMessageCardWithMediaBinding.inflate(layoutInflater, root, false) }
    ) {
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val mediaAdapter = MediaAdapter() { mediaPosition ->
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                mediaListener(position, mediaPosition)
            }
        }
        val markwon = Markwon.builder(context)
            .usePlugin(LinkifyPlugin.create())
            .build()

        fun userClicked() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                userNameListener(position)
            }
        }

        fun showTime() {
            Toast.makeText(
                context.applicationContext,
                timeAgoString(context, item.message.timestamp()),
                Toast.LENGTH_SHORT
            ).show()
        }

        with(binding) {
            text.movementMethod = LinkMovementMethod.getInstance()
            date.setOnClickListener {
                userClicked()
            }
            date.setOnLongClickListener {
                showTime()
                true
            }
            user.setOnClickListener {
                userClicked()
            }
            user.setOnLongClickListener {
                showTime()
                true
            }
            with(mediaList) {
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
        }

        bind {
            val message = item.message
            mediaAdapter.items = message.content.media

            with(binding) {
                markwon.setMarkdown(text, message.text)

                user.newText = message.user
                date.newText = item.message.timestamp().formatDateTime()
                id.newText = message.id

                comments.newText = message.replyCount.toString()
                recommends.newText = message.recommendations.size.toString()

                Glide.with(context)
                    .load(String.format(BuildConfig.USER_AVA_THUMB_URL, message.user))
                    .transform(CircleCrop())
                    .into(ava)
            }
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

class MessageAdapter(
    userNameListener: (Int) -> Unit,
    mediaListener: (Int, Int) -> Unit
) : AsyncListDifferDelegationAdapter<MessageListItem>(itemCallback) {
    init {
        delegatesManager.apply {
            addDelegate(messageDelegate(userNameListener))
            addDelegate(messageWithMediaDelegate(userNameListener, mediaListener))
        }
    }
}
