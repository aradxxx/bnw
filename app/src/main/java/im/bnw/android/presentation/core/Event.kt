package im.bnw.android.presentation.core

import androidx.annotation.StringRes
import im.bnw.android.presentation.settings.adapter.SettingsItem

interface Event

object RemoveMessageFromLocalStorage : Event
object RemoveReplyFromLocalStorage : Event
object LogoutEvent : Event

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

data class ScrollTo(
    val position: Int = 0
) : Event

data class SettingsDialogEvent(
    val title: Int,
    val currentItem: SettingsItem,
    val items: List<SettingsItem>,
) : Event

data class CursorToEnd(
    val text: String
) : Event
