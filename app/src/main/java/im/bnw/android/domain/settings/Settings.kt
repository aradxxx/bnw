package im.bnw.android.domain.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Settings(
    val incognito: Boolean,
    val theme: ThemeSettings,
    val language: LanguageSettings,
) : Parcelable

@Parcelize
open class Setting : Parcelable

sealed class ThemeSettings(val value: String) : Setting() {
    object Default : ThemeSettings("")
    object Light : ThemeSettings("light")
    object Dark : ThemeSettings("dark")
}

sealed class LanguageSettings(val value: String) : Setting() {
    object Default : LanguageSettings("")
    object English : LanguageSettings("en")
    object Russian : LanguageSettings("ru")
}
