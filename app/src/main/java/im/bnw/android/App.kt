package im.bnw.android

import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.android.AppReducer
import com.github.terrakok.modo.android.LogReducer
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

    companion object {
        lateinit var modo: Modo
            private set
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree("DDD"))
        }
        Lingver.init(this, localeStore)
        modo = Modo(buildReducer())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder()
            .bindContext(this)
            .build()
        component.inject(this)
        return component
    }

    private fun buildReducer(): NavigationReducer {
        return if (BuildConfig.DEBUG) {
            LogReducer(AppReducer(this@App, MultiReducer()))
        } else {
            AppReducer(this@App, MultiReducer())
        }
    }
}
