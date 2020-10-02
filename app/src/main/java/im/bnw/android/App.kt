package im.bnw.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import im.bnw.android.presentation.util.DebugTree
import timber.log.Timber

@HiltAndroidApp
open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree("DDD"))
    }
}
