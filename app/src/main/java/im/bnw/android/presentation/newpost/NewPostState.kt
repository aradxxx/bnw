package im.bnw.android.presentation.newpost

import im.bnw.android.presentation.core.State
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewPostState(
    val text: String = "",
    val asAnon: Boolean = false,
    val sendEnabled: Boolean = false
) : State
