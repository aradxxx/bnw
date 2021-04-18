package im.bnw.android.presentation.util

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.externalForward
import com.github.terrakok.modo.forward

@Suppress("SpreadOperator")
fun Modo.extForward(screen: Screen, vararg screens: Screen) {
    if (state.chain.lastOrNull() is MultiScreen) {
        externalForward(screen, *screens)
    } else {
        forward(screen, *screens)
    }
}
