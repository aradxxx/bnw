package im.bnw.android.di.app

import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowRouterFactory
import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.navigation.AppRouter
import javax.inject.Singleton

@Module
class NavigationModule {
    @Provides
    @Singleton
    fun provideRouterFactory() = object : FlowRouterFactory<AppRouter> {
        override fun create(): AppRouter = AppRouter()
    }

    @Provides
    @Singleton
    fun provideCiceroneTab(tabRouterFactory: FlowRouterFactory<AppRouter>) =
        FlowCicerone(tabRouterFactory)
}
