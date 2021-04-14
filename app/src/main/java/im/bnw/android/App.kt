package im.bnw.android

import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.LocaleStore
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import im.bnw.android.di.app.AppComponent
import im.bnw.android.di.app.DaggerAppComponent
import im.bnw.android.presentation.util.DebugTree
import timber.log.Timber
import javax.inject.Inject

class App : DaggerApplication() {
    @Inject
    lateinit var localeStore: LocaleStore

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree("DDD"))
        }
        Lingver.init(this, localeStore)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder()
            .bindContext(this)
            .build()
        component.inject(this)
        return component
    }
}
