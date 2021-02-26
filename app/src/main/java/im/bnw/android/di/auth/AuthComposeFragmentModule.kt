package im.bnw.android.di.auth

import androidx.lifecycle.ViewModel
import com.github.aradxxx.ciceroneflow.NavigationContainer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.auth.AuthComposeFragment
import im.bnw.android.presentation.auth.AuthState
import im.bnw.android.presentation.auth.AuthViewModel
import im.bnw.android.presentation.core.navigation.AppRouter

@Module(includes = [AuthComposeFragmentModule.ViewModelModule::class])
class AuthComposeFragmentModule {
    @Module(includes = [AuthModule::class])
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(AuthViewModel::class)
        fun bindViewModel(viewModel: AuthViewModel): ViewModel
    }

    @Provides
    fun provideRestoredState(fragment: AuthComposeFragment): AuthState? {
        return null
    }

    @Provides
    fun provideRouter(fragment: AuthComposeFragment): AppRouter {
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
        throw IllegalStateException("Can't provide router for " + fragment::class.simpleName)
    }
}
