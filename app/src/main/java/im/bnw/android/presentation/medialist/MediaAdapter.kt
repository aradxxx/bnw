package im.bnw.android.presentation.medialist

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.R
import im.bnw.android.domain.message.Media
import im.bnw.android.presentation.messages.adapter.MessageItem
import im.bnw.android.presentation.messages.adapter.MessageListItem
import im.bnw.android.presentation.messages.adapter.messageDelegate
import im.bnw.android.presentation.messages.adapter.messageWithMediaDelegate
import im.bnw.android.presentation.util.itemCallback
import kotlinx.android.synthetic.main.fragment_image_view.*
import kotlinx.android.synthetic.main.item_media.*
import kotlinx.android.synthetic.main.item_message_card.*

fun mediaDelegate(listener: (Int) -> Unit) =
    adapterDelegateLayoutContainer<Media, Media>(
        R.layout.item_media,
    ) {
        media.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener(position)
            }
        }

        bind {
            if (!item.isYoutube()) {
                Glide.with(context)
                    .load(item.fullUrl)
                    .into(media)
            } else {
                Glide.with(context)
                    .load(item.youtubePreviewLink())
                    .into(media)
            }
        }
    }

val mediaCallback = itemCallback<Media>(
    areItemsTheSame = { oldItem, newItem ->
        oldItem == newItem
    }
)

class MediaAdapter(listener: (Int) -> Unit) :
    AsyncListDifferDelegationAdapter<Media>(mediaCallback) {
    init {
        delegatesManager.addDelegate(mediaDelegate(listener))
    }
}
