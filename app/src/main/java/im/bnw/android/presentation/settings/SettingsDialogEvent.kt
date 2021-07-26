package im.bnw.android.presentation.settings

import im.bnw.android.presentation.settings.adapter.SettingsItem

data class SettingsDialogEvent(
    val title: Int,
    val currentItem: SettingsItem,
    val items: List<SettingsItem>,
)
