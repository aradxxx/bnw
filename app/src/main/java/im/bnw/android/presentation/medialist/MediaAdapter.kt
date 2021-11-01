package im.bnw.android.presentation.medialist

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.R
import im.bnw.android.databinding.ItemMediaBinding
import im.bnw.android.domain.message.Media
import im.bnw.android.presentation.util.doIfPositionValid
import im.bnw.android.presentation.util.itemCallback

fun mediaDelegate(listener: (Int) -> Unit) =
    adapterDelegateViewBinding<Media, Media, ItemMediaBinding>(
        { layoutInflater, root -> ItemMediaBinding.inflate(layoutInflater, root, false) }
    ) {
        binding.media.setOnClickListener {
            doIfPositionValid(listener)
        }

        bind {
            with(binding) {
                val image = if (item.isYoutube()) {
                    item.youtubePreviewLink()
                } else {
                    item.previewUrl
                }
                playButton.isVisible = item.isYoutube()
                Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.ic_media_placeholder)
                    .error(R.drawable.ic_media_load_error)
                    .into(media)
            }
        }

        onViewRecycled {
            binding.media.setImageResource(R.drawable.ic_media_placeholder)
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
