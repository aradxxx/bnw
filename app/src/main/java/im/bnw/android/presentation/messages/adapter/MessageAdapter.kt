@file:Suppress("MagicNumber")

package im.bnw.android.presentation.messages.adapter

import android.graphics.Rect
import android.os.Parcelable
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.databinding.ItemMessageCardBinding
import im.bnw.android.databinding.ItemMessageCardWithMediaBinding
import im.bnw.android.presentation.core.markwon.BnwLinkifyPlugin
import im.bnw.android.presentation.medialist.MediaAdapter
import im.bnw.android.presentation.messagedetails.adapter.ReplyItem
import im.bnw.android.presentation.messages.MessageClickListener
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.itemCallback
import im.bnw.android.presentation.util.loadCircleAvatar
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.timeAgoString
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin

fun messageDelegate(
    messageClickListener: MessageClickListener,
    cardRadius: Float,
) = adapterDelegateViewBinding<MessageItem, MessageListItem, ItemMessageCardBinding>(
    viewBinding = { layoutInflater, root ->
        ItemMessageCardBinding.inflate(layoutInflater, root, false)
    },
    on = { item, _, _ ->
        item is MessageItem && item.message.media.isEmpty()
    }
) {
    val markwon = Markwon.builder(context)
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(BnwLinkifyPlugin)
        .build()

    fun userClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.userClicked(position)
        }
    }

    fun showTime() {
        Toast.makeText(
            context.applicationContext,
            timeAgoString(context, item.message.timestamp),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cardClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.cardClicked(position)
        }
    }

    fun saveMessageClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.saveClicked(position)
        }
    }

    with(binding) {
        root.radius = cardRadius
        userProfile.setOnClickListener {
            userClicked()
        }
        userProfile.setOnLongClickListener {
            showTime()
            true
        }
        listOf(root, text, footer.id, footer.comments, footer.recommends).forEach {
            it.setOnClickListener {
                cardClicked()
            }
        }
        footer.save.setOnClickListener {
            saveMessageClicked()
        }
    }

    bind {
        val message = item.message
        with(binding) {
            markwon.setMarkdown(text, message.text)
            user.newText = message.user
            date.newText = item.message.timestamp.formatDateTime()
            with(footer) {
                id.newText = message.id
                comments.newText = message.replyCount.toString()
                recommends.newText = message.recommendations.count().toString()
                save.isChecked = item.saved
            }
            avatar.loadCircleAvatar(context, message.user)
        }
    }
}

@Suppress("LongMethod", "ComplexMethod")
fun messageWithMediaDelegate(
    messageClickListener: MessageClickListener,
    cardRadius: Float,
    mediaHeight: Int,
    savedInstanceStates: MutableMap<String, Parcelable?>,
) = adapterDelegateViewBinding<MessageItem, MessageListItem, ItemMessageCardWithMediaBinding>(
    viewBinding = { layoutInflater, root ->
        ItemMessageCardWithMediaBinding.inflate(layoutInflater, root, false)
    },
    on = { item, _, _ ->
        item is MessageItem && item.message.media.isNotEmpty()
    }
) {
    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    val mediaAdapter = MediaAdapter { mediaPosition ->
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.mediaClicked(position, mediaPosition)
        }
    }
    val markwon = Markwon.builder(context)
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(BnwLinkifyPlugin)
        .build()

    fun userClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.userClicked(position)
        }
    }

    fun showTime() {
        Toast.makeText(
            context.applicationContext,
            timeAgoString(context, item.message.timestamp),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cardClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.cardClicked(position)
        }
    }

    fun saveMessageClicked() {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            messageClickListener.saveClicked(position)
        }
    }

    fun saveInstanceState(messageId: String) {
        savedInstanceStates[messageId] = linearLayoutManager.onSaveInstanceState()
    }

    fun restoreInstanceState(messageId: String) {
        val savedState = savedInstanceStates[messageId]
        if (savedState != null) {
            linearLayoutManager.onRestoreInstanceState(savedState)
        } else {
            linearLayoutManager.scrollToPosition(0)
        }
    }

    with(binding) {
        root.radius = cardRadius
        userProfile.setOnClickListener {
            userClicked()
        }
        userProfile.setOnLongClickListener {
            showTime()
            true
        }
        listOf(root, text, footer.id, footer.comments, footer.recommends).forEach {
            it.setOnClickListener {
                cardClicked()
            }
        }
        footer.save.setOnClickListener {
            saveMessageClicked()
        }
        with(mediaList) {
            updateLayoutParams {
                height = mediaHeight
            }
            layoutManager = linearLayoutManager
            adapter = mediaAdapter
            addItemDecoration(mediaItemDecorator)
        }
    }
    bind {
        val message = item.message
        mediaAdapter.items = message.media

        with(binding) {
            markwon.setMarkdown(text, message.text)
            user.newText = message.user
            date.newText = item.message.timestamp.formatDateTime()
            with(footer) {
                id.newText = message.id
                comments.newText = message.replyCount.toString()
                recommends.newText = message.recommendations.count().toString()
                save.isChecked = item.saved
            }
            avatar.loadCircleAvatar(context, message.user)
        }
        restoreInstanceState(item.message.id)
    }
    onViewRecycled {
        saveInstanceState(item.message.id)
    }
}

val messageItemDecorator = object : RecyclerView.ItemDecoration() {
    val normal = 16.dpToPx
    val half = 8.dpToPx
    override fun getItemOffsets(
        outRect: Rect,
        itemPosition: Int,
        parent: RecyclerView
    ) {
        val top = if (itemPosition == 0) {
            normal
        } else {
            half
        }
        val bottom = if (itemPosition + 1 == parent.adapter?.itemCount) {
            normal
        } else {
            0
        }
        outRect.set(half, top, half, bottom)
    }
}

val mediaItemDecorator = object : RecyclerView.ItemDecoration() {
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

val messageListItemDiffCallback: DiffUtil.ItemCallback<MessageListItem> = itemCallback(
    areItemsTheSame = { oldItem, newItem ->
        oldItem.id == newItem.id
    },
    areContentsTheSame = { oldItem, newItem ->
        oldItem.areContentsTheSame(newItem)
    }
)

fun MessageListItem.areContentsTheSame(other: MessageListItem): Boolean {
    return when {
        this is MessageItem && other is MessageItem -> {
            this.areContentsTheSame(other)
        }
        this is ReplyItem && other is ReplyItem -> {
            this.areContentsTheSame(other)
        }
        else -> false
    }
}

fun MessageItem.areContentsTheSame(other: MessageItem): Boolean {
    return saved == other.saved &&
        message.id == other.message.id &&
        message.text == other.message.text &&
        message.user == other.message.user &&
        message.timestamp == other.message.timestamp &&
        message.replyCount == other.message.replyCount &&
        message.recommendations == other.message.recommendations
}

fun ReplyItem.areContentsTheSame(other: ReplyItem): Boolean {
    return saved == other.saved &&
        reply.id == other.reply.id &&
        reply.text == other.reply.text &&
        reply.user == other.reply.user &&
        reply.timestamp == other.reply.timestamp
}

class MessageAdapter(
    messageClickListener: MessageClickListener,
    cardRadius: Float,
    mediaHeight: Int,
) : AsyncListDifferDelegationAdapter<MessageListItem>(messageListItemDiffCallback) {
    private val savedInstanceStates: MutableMap<String, Parcelable?> = mutableMapOf()

    init {
        delegatesManager.apply {
            addDelegate(
                messageDelegate(
                    messageClickListener,
                    cardRadius,
                )
            )
            addDelegate(
                messageWithMediaDelegate(
                    messageClickListener,
                    cardRadius,
                    mediaHeight,
                    savedInstanceStates,
                )
            )
        }
    }
}
