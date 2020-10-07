package im.bnw.android.presentation.medialist

import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import im.bnw.android.R
import im.bnw.android.domain.message.Media
import im.bnw.android.presentation.util.itemCallback
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

val mediaCallback = itemCallback<Media>(
    areItemsTheSame = { oldItem, newItem ->
        oldItem == newItem
    }
)

class MediaAdapter : AsyncListDifferDelegationAdapter<Media>(mediaCallback) {
    init {
        delegatesManager.addDelegate(mediaDelegate)
    }
}
