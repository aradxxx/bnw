package im.bnw.android.presentation.profile

import androidx.annotation.StringRes
import im.bnw.android.R
import im.bnw.android.presentation.profile.adapter.SettingsItem

sealed class ThemeItem(@StringRes nameResId: Int) : SettingsItem(nameResId) {
    object Default : ThemeItem(R.string.system_default)
    object Light : ThemeItem(R.string.light_theme)
    object Dark : ThemeItem(R.string.dark_theme)
}

sealed class LanguageItem(@StringRes nameResId: Int) : SettingsItem(nameResId) {
    object Default : LanguageItem(R.string.system_default)
    object English : LanguageItem(R.string.english)
    object Russian : LanguageItem(R.string.russian)
}
