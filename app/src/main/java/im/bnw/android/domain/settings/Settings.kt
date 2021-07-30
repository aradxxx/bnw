package im.bnw.android.domain.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Settings(
    val incognito: Boolean,
    val scrollToReplies: Boolean,
    val transitionAnimations: Boolean,
    val theme: ThemeSettings,
    val language: LanguageSettings,
) : Parcelable

@Parcelize
open class Setting : Parcelable

sealed class ThemeSettings(val value: String) : Setting() {
    @Parcelize
    object Default : ThemeSettings("")

    @Parcelize
    object Light : ThemeSettings("light")

    @Parcelize
    object Dark : ThemeSettings("dark")
}

sealed class LanguageSettings(val value: String) : Setting() {
    @Parcelize
    object Default : LanguageSettings("")

    @Parcelize
    object English : LanguageSettings("en")

    @Parcelize
    object Russian : LanguageSettings("ru")
}
