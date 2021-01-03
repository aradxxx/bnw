package im.bnw.android.presentation.util

import im.bnw.android.presentation.core.State

inline fun <reified S : State> State.nullOr(): S? =
    if (this is S) {
        this
    } else {
        null
    }
