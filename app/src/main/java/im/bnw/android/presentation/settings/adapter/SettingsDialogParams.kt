package im.bnw.android.presentation.settings.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsDialogParams(
    val title: String = "",
    val currentItem: SettingsItem,
    val items: List<SettingsItem>,
) : Parcelable
