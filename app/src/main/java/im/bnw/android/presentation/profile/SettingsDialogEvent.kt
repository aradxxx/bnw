package im.bnw.android.presentation.profile

import im.bnw.android.presentation.profile.adapter.SettingsItem

data class SettingsDialogEvent(
    val title: Int,
    val currentItem: SettingsItem,
    val items: List<SettingsItem>,
)
