package im.bnw.android.presentation.media_list

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.R
import im.bnw.android.domain.message.Media
import kotlinx.android.synthetic.main.item_media.*

val mediaDelegate = adapterDelegateLayoutContainer<Media, Media>(
    R.layout.item_media
) {
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

val mediaCallback: DiffUtil.ItemCallback<Media> = object : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem == newItem
    }
}

class MediaAdapter : AsyncListDifferDelegationAdapter<Media>(mediaCallback) {
    init {
        delegatesManager.addDelegate(mediaDelegate)
    }
}
