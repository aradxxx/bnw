package im.bnw.android.di.app

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import im.bnw.android.App
import im.bnw.android.di.ActivityBindingModule
import im.bnw.android.di.core.ViewModelFactoryModule
import im.bnw.android.di.settings.SettingsModule
import im.bnw.android.di.usermanager.UserManagerModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBindingModule::class,
        ViewModelFactoryModule::class,
        NetworkModule::class,
        DataModule::class,
        UserManagerModule::class,
        SettingsModule::class,
        NavigationModule::class,
    ]
)
interface AppComponent : AndroidInjector<DaggerApplication> {
    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): AppComponent
    }
}
