package im.bnw.android.di.app

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import im.bnw.android.presentation.core.navigation.AppRouter
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabRouterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideRouterFactory(): TabRouterFactory<AppRouter> = TabRouterFactory {
        AppRouter()
    }

    @Provides
    @Singleton
    fun provideCiceroneTab(tabRouterFactory: TabRouterFactory<AppRouter>) =
        TabCicerone(tabRouterFactory)
}
