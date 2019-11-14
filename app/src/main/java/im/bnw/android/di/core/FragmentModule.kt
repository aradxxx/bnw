package im.bnw.android.di.core

import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.State
import im.bnw.android.presentation.core.navigation.AppRouter
import ru.aradxxx.ciceronetabs.NavigationContainer
import ru.aradxxx.ciceronetabs.TabCicerone

@Module
abstract class FragmentModule<F : BaseFragment<VM, S>, VM : BaseViewModel<S>, S : State> {
    @Provides
    fun provideRestoredState(fragment: F): S? {
        return fragment.restoredState
    }

    @Provides
    fun provideRouter(fragment: F, cicerone: TabCicerone<AppRouter>): AppRouter {
        val activity = fragment.requireActivity()
        val parent = fragment.parentFragment
        when {
            parent is NavigationContainer<*> -> {
                val router = parent.router()
                if (router is AppRouter) return router
            }
            activity is NavigationContainer<*> -> {
                val router = activity.router()
                if (router is AppRouter) return router
            }
        }
        throw RuntimeException("Can't provide router for " + fragment::class.simpleName)
    }
}
