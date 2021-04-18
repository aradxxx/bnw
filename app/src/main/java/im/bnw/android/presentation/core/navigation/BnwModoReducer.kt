package im.bnw.android.presentation.core.navigation

import com.github.terrakok.modo.ExternalForward
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState

@Suppress("SpreadOperator")
class BnwModoReducer(private val origin: NavigationReducer) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState =
        when (action) {
            is ExternalForward -> NavigationState(
                state.chain + listOf(action.screen, *action.screens)
            )
            else -> {
                origin.invoke(action, state)
            }
        }
}
