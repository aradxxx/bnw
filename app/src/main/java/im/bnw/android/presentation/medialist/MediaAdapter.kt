package im.bnw.android.presentation.medialist

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.R
import im.bnw.android.domain.message.Media
import im.bnw.android.presentation.util.itemCallback
import kotlinx.android.synthetic.main.item_media.*

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
                play_button.isVisible = false
                Glide.with(context)
                    .load(item.fullUrl)
                    .into(media)
            } else {
                play_button.isVisible = true
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
