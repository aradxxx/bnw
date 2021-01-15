package im.bnw.android.presentation.auth

import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthState(
    val userName: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val authorized: Boolean = false,
) : State
