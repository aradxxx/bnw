package im.bnw.android.presentation.user

import im.bnw.android.presentation.user.adapter.SettingsItem

data class SettingsDialogEvent(
    val title: Int,
    val currentItem: SettingsItem,
    val items: List<SettingsItem>,
)
