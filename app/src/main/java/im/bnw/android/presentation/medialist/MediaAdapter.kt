package im.bnw.android.presentation.medialist

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.databinding.ItemMediaBinding
import im.bnw.android.domain.message.Media
import im.bnw.android.presentation.util.itemCallback

fun mediaDelegate(listener: (Int) -> Unit) =
    adapterDelegateViewBinding<Media, Media, ItemMediaBinding>(
        { layoutInflater, root -> ItemMediaBinding.inflate(layoutInflater, root, false) }
    ) {
        binding.media.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener(position)
            }
        }

        bind {
            with(binding) {
                val image = if (item.isYoutube()) {
                    item.youtubePreviewLink()
                } else {
                    item.fullUrl
                }
                playButton.isVisible = item.isYoutube()
                Glide.with(context)
                    .load(image)
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
