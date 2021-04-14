package im.bnw.android.presentation.profile.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import im.bnw.android.databinding.ItemSettingsDialogCardBinding
import im.bnw.android.presentation.util.newText

fun settingsDelegate(currentItem: SettingsItem, listener: (SettingsItem) -> Unit) =
    adapterDelegateViewBinding<SettingsItem, SettingsItem, ItemSettingsDialogCardBinding>(
        { layoutInflater, root ->
            ItemSettingsDialogCardBinding.inflate(
                layoutInflater,
                root,
                false
            )
        }
    ) {
        fun itemClicked() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener(item)
            }
        }

        with(binding) {
            itemText.setOnClickListener {
                itemClicked()
            }
        }

        bind {
            binding.itemText.newText = getString(item.nameResId)
            binding.itemText.isChecked = item == currentItem
        }
    }

val itemCallback: DiffUtil.ItemCallback<SettingsItem> =
    object : DiffUtil.ItemCallback<SettingsItem>() {
        override fun areItemsTheSame(
            oldItem: SettingsItem,
            newItem: SettingsItem
        ): Boolean {
            return oldItem.nameResId == newItem.nameResId
        }

        override fun areContentsTheSame(
            oldItem: SettingsItem,
            newItem: SettingsItem
        ): Boolean {
            return oldItem.nameResId == newItem.nameResId
        }
    }

class SettingsAdapter(
    currentItem: SettingsItem,
    listener: (SettingsItem) -> Unit,
) : AsyncListDifferDelegationAdapter<SettingsItem>(itemCallback) {
    init {
        delegatesManager.addDelegate(settingsDelegate(currentItem, listener))
    }
}
