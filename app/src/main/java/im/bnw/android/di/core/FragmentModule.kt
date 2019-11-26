package im.bnw.android.di.core

import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.core.navigation.AppRouter
import ru.aradxxx.ciceronetabs.NavigationContainer

@Module
abstract class FragmentModule<F : BaseFragment<VM, S>, VM : BaseViewModel<S>, S : State> {
    @Provides
    fun provideRestoredState(fragment: F): S? {
        return fragment.restoredState
    }

    @Provides
    fun provideRouter(fragment: F): AppRouter {
        val parentFragment = fragment.parentFragment
        if (parentFragment is NavigationContainer<*>) {
            val router = parentFragment.router()
            if (router is AppRouter) return router
        }

        val activity = fragment.requireActivity()
        if (activity is NavigationContainer<*>) {
            val router = activity.router()
            if (router is AppRouter) return router
        }
        throw RuntimeException("Can't provide router for " + fragment::class.simpleName)
    }
}
