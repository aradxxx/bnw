package im.bnw.android.presentation.main

import android.os.Bundle
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import com.github.aradxxx.ciceroneflow.NavigationContainer
import im.bnw.android.R
import im.bnw.android.databinding.ActivityMainBinding
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.viewBinding
import javax.inject.Inject

class MainActivity :
    BaseActivity<MainViewModel, MainState>(R.layout.activity_main, MainViewModel::class.java),
    NavigationContainer<AppRouter> {
    @Inject
    lateinit var flowCicerone: FlowCicerone<AppRouter>
    lateinit var navigator: FlowNavigator<AppRouter>
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = FlowNavigator(this, R.id.container, flowCicerone)
        if (savedInstanceState == null) {
            viewModel.startNavigation()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        flowCicerone.mainCicerone().getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        flowCicerone.mainCicerone().getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun router(): AppRouter {
        return flowCicerone.mainRouter()
    }
}
