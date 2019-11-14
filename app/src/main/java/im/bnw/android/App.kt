package im.bnw.android

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import im.bnw.android.di.app.AppComponent
import im.bnw.android.di.app.DaggerAppComponent
import timber.log.Timber

class App : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder()
            .bindContext(this)
            .build()
        component.inject(this)
        return component
    }
}
