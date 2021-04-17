package im.bnw.android.presentation.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileScreenParams(
    val userName: String
) : Parcelable
