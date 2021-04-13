package im.bnw.android.presentation.profile.adapter

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
open class SettingsItem(
    @StringRes
    val nameResId: Int,
) : Parcelable
