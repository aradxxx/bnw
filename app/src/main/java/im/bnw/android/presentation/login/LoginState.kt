package im.bnw.android.presentation.login

import im.bnw.android.presentation.core.State
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginState(
    val userName: String = "",
    val password: String = "",
    val loading: Boolean = false,
) : State
