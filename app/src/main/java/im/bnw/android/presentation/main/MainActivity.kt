package im.bnw.android.presentation.main

import android.os.Bundle
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.navigation.AppRouter
import ru.aradxxx.ciceronetabs.NavigationContainer
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel, MainState>(
    R.layout.activity_main,
    MainViewModel::class.java
), NavigationContainer<AppRouter> {
    @Inject
    lateinit var tabCicerone: TabCicerone<AppRouter>
    lateinit var navigator: TabNavigator<AppRouter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = TabNavigator(this, tabCicerone, R.id.container)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        tabCicerone.activityCicerone().navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        tabCicerone.activityCicerone().navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun router(): AppRouter {
        return tabCicerone.activityRouter()
    }
}
