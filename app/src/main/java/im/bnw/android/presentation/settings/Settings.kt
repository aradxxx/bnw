package im.bnw.android.presentation.settings

import androidx.annotation.StringRes
import im.bnw.android.R
import im.bnw.android.presentation.settings.adapter.SettingsItem
import kotlinx.parcelize.Parcelize

sealed class ThemeItem(@StringRes nameResId: Int) : SettingsItem(nameResId) {
    @Parcelize
    object Default : ThemeItem(R.string.system_default)

    @Parcelize
    object Light : ThemeItem(R.string.light_theme)

    @Parcelize
    object Dark : ThemeItem(R.string.dark_theme)
}

sealed class LanguageItem(@StringRes nameResId: Int) : SettingsItem(nameResId) {
    @Parcelize
    object Default : LanguageItem(R.string.system_default)

    @Parcelize
    object English : LanguageItem(R.string.english)

    @Parcelize
    object Russian : LanguageItem(R.string.russian)
}

sealed class TabSettingsItem(@StringRes nameResId: Int) : SettingsItem(nameResId) {
    @Parcelize
    object Messages : TabSettingsItem(R.string.general)

    @Parcelize
    object Hot : TabSettingsItem(R.string.today)

    @Parcelize
    object User : TabSettingsItem(R.string.profile)
}

