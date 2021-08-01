package im.bnw.android.presentation.user

import android.os.Parcelable
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

sealed class UserState : State {
    @Parcelize
    object Loading : UserState()

    @Parcelize
    data class Failed(
        val throwable: Throwable
    ) : UserState()

    @Parcelize
    data class Authorized(
        val user: User,
        val savedDetails: SavedDetails,
    ) : UserState()

    @Parcelize
    data class Unauthorized(
        val savedDetails: SavedDetails,
    ) : UserState()
}

@Parcelize
data class SavedDetails(
    val messagesCount: Int,
    val repliesCount: Int,
) : Parcelable
