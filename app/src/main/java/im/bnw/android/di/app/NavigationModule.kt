package im.bnw.android.di.app

import android.content.Context
import com.github.terrakok.modo.BuildConfig
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.android.AppReducer
import com.github.terrakok.modo.android.LogReducer
import dagger.Module
import dagger.Provides
import im.bnw.android.presentation.core.navigation.BnwModoReducer
import javax.inject.Singleton

@Module
class NavigationModule {
    @Provides
    @Singleton
    fun provideModo(context: Context): Modo {
        val reducer = if (BuildConfig.DEBUG) {
            LogReducer(BnwModoReducer(AppReducer(context, MultiReducer())))
        } else {
            BnwModoReducer(AppReducer(context, MultiReducer()))
        }
        return Modo(reducer)
    }
}
