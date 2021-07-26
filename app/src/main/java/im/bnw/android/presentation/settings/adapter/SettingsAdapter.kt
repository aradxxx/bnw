package im.bnw.android.presentation.settings.adapter

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.databinding.ItemSettingsDialogCardBinding
import im.bnw.android.presentation.util.itemCallback
import im.bnw.android.presentation.util.newText

fun settingsDelegate(
    currentItem: SettingsItem,
    listener: (SettingsItem) -> Unit,
) = adapterDelegateViewBinding<SettingsItem, SettingsItem, ItemSettingsDialogCardBinding>(
    { layoutInflater, root ->
        ItemSettingsDialogCardBinding.inflate(layoutInflater, root, false)
    }
) {
    fun itemClicked() {
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        listener(item)
    }

    binding.value.setOnClickListener {
        itemClicked()
    }

    bind {
        with(binding.value) {
            newText = getString(item.nameResId)
            isChecked = item == currentItem
        }
    }
}

val settingsItemCallback = itemCallback<SettingsItem>(
    areItemsTheSame = { oldItem, newItem ->
        oldItem == newItem
    },
    areContentsTheSame = { oldItem, newItem ->
        oldItem.nameResId == newItem.nameResId
    },
)

class SettingsAdapter(
    currentItem: SettingsItem,
    listener: (SettingsItem) -> Unit,
) : AsyncListDifferDelegationAdapter<SettingsItem>(settingsItemCallback) {
    init {
        delegatesManager.addDelegate(
            settingsDelegate(
                currentItem,
                listener,
            )
        )
    }
}