package im.bnw.android.presentation.settings.adapter

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.databinding.ItemSettingsDialogCardBinding
import im.bnw.android.presentation.util.itemCallback
import im.bnw.android.presentation.util.newText

fun settingsDelegate(
    currentItem: SettingsItem,
    chosenItemListener: (SettingsItem) -> Unit,
) = adapterDelegateViewBinding<SettingsItem, SettingsItem, ItemSettingsDialogCardBinding>(
    { layoutInflater, root ->
        ItemSettingsDialogCardBinding.inflate(layoutInflater, root, false)
    }
) {
    fun itemClicked() {
        if (bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        chosenItemListener(item)
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
    chosenItemListener: (SettingsItem) -> Unit,
) : AsyncListDifferDelegationAdapter<SettingsItem>(settingsItemCallback) {
    init {
        delegatesManager.addDelegate(
            settingsDelegate(
                currentItem,
                chosenItemListener,
            )
        )
    }
}
