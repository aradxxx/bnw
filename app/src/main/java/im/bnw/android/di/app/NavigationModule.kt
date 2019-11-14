package im.bnw.android.di.app

import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.navigation.AppRouter
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabRouterFactory
import javax.inject.Singleton

@Module
class NavigationModule {
    @Provides
    @Singleton
    fun provideRouterFactory(): TabRouterFactory<AppRouter> = TabRouterFactory {
        AppRouter()
    }

    @Provides
    @Singleton
    fun provideCiceroneTab(tabRouterFactory: TabRouterFactory<AppRouter>): TabCicerone<AppRouter> =
        TabCicerone(tabRouterFactory)
}
