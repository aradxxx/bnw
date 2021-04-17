@file:Suppress("MagicNumber")

package im.bnw.android.presentation.messagedetails.adapter

import android.graphics.Rect
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.BuildConfig
import im.bnw.android.databinding.ItemReplyCardBinding
import im.bnw.android.databinding.ItemReplyCardWithMediaBinding
import im.bnw.android.presentation.core.markwon.BnwLinkifyPlugin
import im.bnw.android.presentation.medialist.MediaAdapter
import im.bnw.android.presentation.messages.adapter.MessageListItem
import im.bnw.android.presentation.messages.adapter.messageDelegate
import im.bnw.android.presentation.messages.adapter.messageListItemDiffCallback
import im.bnw.android.presentation.messages.adapter.messageWithMediaDelegate
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.timeAgoString
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin
import java.lang.Integer.min

fun replyDelegate(
    userNameListener: (Int) -> Unit,
    replyCardClickListener: (Int) -> Unit
) = adapterDelegateViewBinding<ReplyItem, MessageListItem, ItemReplyCardBinding>(
    viewBinding = { layoutInflater, root ->
        ItemReplyCardBinding.inflate(layoutInflater, root, false)
    },
    on = { item, _, _ ->
        item is ReplyItem && item.reply.media.isEmpty()
    }
) {
    val markwon = Markwon.builder(context)
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(BnwLinkifyPlugin)
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
            timeAgoString(context, item.reply.timestamp),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cardClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            replyCardClickListener(position)
        }
    }
    with(binding) {
        userDate.setOnClickListener {
            userClicked()
        }
        userDate.setOnLongClickListener {
            showTime()
            true
        }
        root.setOnClickListener {
            cardClicked()
        }
        text.setOnClickListener {
            cardClicked()
        }
    }
    bind {
        val reply = item.reply
        with(binding) {
            markwon.setMarkdown(text, reply.text)
            replyTo.newText = reply.replyTo
            replyTo.isVisible = reply.replyTo.isNotEmpty()
            replyToIcon.isVisible = reply.replyTo.isNotEmpty()
            user.newText = reply.user
            date.newText = reply.timestamp.formatDateTime()
            id.newText = reply.id

            Glide.with(context)
                .load(String.format(BuildConfig.USER_AVA_THUMB_URL, reply.user))
                .transform(CircleCrop())
                .into(ava)
        }
    }
}

@Suppress("LongMethod")
fun replyWithMediaDelegate(
    userNameListener: (Int) -> Unit,
    mediaListener: (Int, Int) -> Unit,
    replyCardClickListener: (Int) -> Unit
) = adapterDelegateViewBinding<ReplyItem, MessageListItem, ItemReplyCardWithMediaBinding>(
    viewBinding = { layoutInflater, root ->
        ItemReplyCardWithMediaBinding.inflate(layoutInflater, root, false)
    },
    on = { item, _, _ ->
        item is ReplyItem && item.reply.media.isNotEmpty()
    }
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
        .usePlugin(BnwLinkifyPlugin)
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
            timeAgoString(context, item.reply.timestamp),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cardClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            replyCardClickListener(position)
        }
    }
    with(binding) {
        userDate.setOnClickListener {
            userClicked()
        }
        userDate.setOnLongClickListener {
            showTime()
            true
        }
        root.setOnClickListener {
            cardClicked()
        }
        text.setOnClickListener {
            cardClicked()
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
        val reply = item.reply
        mediaAdapter.items = reply.media

        with(binding) {
            markwon.setMarkdown(text, reply.text)

            user.newText = reply.user
            date.newText = reply.timestamp.formatDateTime()
            id.newText = reply.id
            replyTo.newText = reply.replyTo
            replyTo.isVisible = reply.replyTo.isNotEmpty()
            replyToIcon.isVisible = reply.replyTo.isNotEmpty()

            Glide.with(context)
                .load(String.format(BuildConfig.USER_AVA_THUMB_URL, reply.user))
                .transform(CircleCrop())
                .into(ava)
        }
    }
}

val replyItemDecorator = object : RecyclerView.ItemDecoration() {
    val normal = 8.dpToPx
    val small = 4.dpToPx
    val maxOffset = 180.dpToPx
    override fun getItemOffsets(
        outRect: Rect,
        itemPosition: Int,
        parent: RecyclerView
    ) {
        outRect.right = normal
        outRect.left = if (parent.adapter is ReplyAdapter) {
            val adapter = parent.adapter as ReplyAdapter
            val offset = adapter.items[itemPosition].getOffset()
            min(normal + normal * 2 * offset, maxOffset)
        } else {
            normal
        }

        when {
            itemPosition == 0 -> {
                outRect.left = 0
                outRect.right = 0
                outRect.top = 0
                outRect.bottom = normal * 2
            }
            itemPosition + 1 == parent.adapter?.itemCount -> {
                outRect.top = small
                outRect.bottom = normal
            }
            else -> {
                outRect.top = small
                outRect.bottom = small
            }
        }
    }
}

private fun MessageListItem.getOffset(): Int {
    return when (this) {
        is ReplyItem -> offset
        else -> 0
    }
}

class ReplyAdapter(
    messageCardRadius: Float,
    messageMediaHeight: Int,
    userNameListener: (Int) -> Unit,
    mediaListener: (Int, Int) -> Unit,
    replyCardClickListener: (Int) -> Unit
) : AsyncListDifferDelegationAdapter<MessageListItem>(messageListItemDiffCallback) {
    init {
        delegatesManager.apply {
            addDelegate(
                replyDelegate(
                    userNameListener,
                    replyCardClickListener
                )
            )
            addDelegate(
                replyWithMediaDelegate(
                    userNameListener,
                    mediaListener,
                    replyCardClickListener
                )
            )
            addDelegate(
                messageDelegate(
                    messageCardRadius,
                    {},
                    userNameListener,
                )
            )
            addDelegate(
                messageWithMediaDelegate(
                    messageCardRadius,
                    messageMediaHeight,
                    {},
                    userNameListener,
                    mediaListener
                )
            )
        }
    }
}
