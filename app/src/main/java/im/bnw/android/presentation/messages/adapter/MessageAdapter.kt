@file:Suppress("MagicNumber")

package im.bnw.android.presentation.messages.adapter

import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.core.view.updateLayoutParams
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
import im.bnw.android.presentation.core.markwon.BnwLinkifyPlugin
import im.bnw.android.presentation.medialist.MediaAdapter
import im.bnw.android.presentation.util.dpToPx
import im.bnw.android.presentation.util.formatDateTime
import im.bnw.android.presentation.util.id
import im.bnw.android.presentation.util.itemCallback
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.timeAgoString
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin

private const val STATE_RESTORE_DELAY = 500L
fun messageDelegate(
    cardRadius: Float,
    cardClickListener: (Int) -> Unit,
    userClickListener: (Int) -> Unit,
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
            userClickListener(position)
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
            cardClickListener(position)
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
        root.setOnClickListener {
            cardClicked()
        }
        text.setOnClickListener {
            cardClicked()
        }
    }

    bind {
        val message = item.message
        with(binding) {
            markwon.setMarkdown(text, message.text)

            user.newText = message.user
            date.newText = item.message.timestamp.formatDateTime()
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

@Suppress("LongMethod", "ComplexMethod")
fun messageWithMediaDelegate(
    cardRadius: Float,
    mediaHeight: Int,
    cardClickListener: (Int) -> Unit,
    userClickListener: (Int) -> Unit,
    mediaListener: (Int, Int) -> Unit,
    savedInstanceStates: MutableMap<String, Parcelable?>,
) = adapterDelegateViewBinding<MessageItem, MessageListItem, ItemMessageCardWithMediaBinding>(
    viewBinding = { layoutInflater, root ->
        ItemMessageCardWithMediaBinding.inflate(layoutInflater, root, false)
    },
    on = { item, _, _ ->
        item is MessageItem && item.message.media.isNotEmpty()
    }
) {
    val handler = Handler(Looper.getMainLooper())
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
            userClickListener(position)
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
            cardClickListener(position)
        }
    }

    fun saveInstanceState(messageId: String) {
        savedInstanceStates[messageId] = linearLayoutManager.onSaveInstanceState()
    }

    fun restoreInstanceState(messageId: String) {
        val savedState = savedInstanceStates[messageId]
        if (savedState != null) {
            handler.postDelayed(STATE_RESTORE_DELAY) {
                linearLayoutManager.onRestoreInstanceState(savedState)
            }
        } else {
            linearLayoutManager.scrollToPosition(0)
        }
    }

    with(binding) {
        userProfile.setOnClickListener {
            userClicked()
        }
        userProfile.setOnLongClickListener {
            showTime()
            true
        }
        root.apply {
            radius = cardRadius
            setOnClickListener {
                cardClicked()
            }
        }
        text.setOnClickListener {
            cardClicked()
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
            id.newText = message.id

            comments.newText = message.replyCount.toString()
            recommends.newText = message.recommendations.size.toString()

            Glide.with(context)
                .load(String.format(BuildConfig.USER_AVA_THUMB_URL, message.user))
                .transform(CircleCrop())
                .into(ava)
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
        oldItem == newItem
    }
)

class MessageAdapter(
    cardRadius: Float,
    mediaHeight: Int,
    cardClickListener: (Int) -> Unit,
    userClickListener: (Int) -> Unit,
    mediaListener: (Int, Int) -> Unit,
) : AsyncListDifferDelegationAdapter<MessageListItem>(messageListItemDiffCallback) {
    private val savedInstanceStates: MutableMap<String, Parcelable?> = mutableMapOf()

    init {
        delegatesManager.apply {
            addDelegate(
                messageDelegate(
                    cardRadius,
                    cardClickListener,
                    userClickListener,
                )
            )
            addDelegate(
                messageWithMediaDelegate(
                    cardRadius,
                    mediaHeight,
                    cardClickListener,
                    userClickListener,
                    mediaListener,
                    savedInstanceStates,
                )
            )
        }
    }
}
