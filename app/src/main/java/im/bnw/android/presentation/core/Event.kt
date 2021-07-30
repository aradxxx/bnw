package im.bnw.android.presentation.core

import androidx.annotation.StringRes
import im.bnw.android.presentation.settings.adapter.SettingsItem

interface Event

data class DialogEvent(
    @StringRes
    val title: Int,
    @StringRes
    val message: Int? = null
) : Event

data class BnwApiErrorEvent(
    val description: String
) : Event

data class OpenMediaEvent(
    val urls: List<String>,
    val selectedItem: String = urls.first()
) : Event

object RemoveMessageFromLocalStorage : Event
object ScrollToTop : Event

data class SettingsDialogEvent(
    val title: Int,
    val currentItem: SettingsItem,
    val items: List<SettingsItem>,
) : Event

object LogoutEvent : Event
