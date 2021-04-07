package im.bnw.android.presentation.main

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.*
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import com.github.aradxxx.ciceroneflow.NavigationContainer
import dev.chrisbanes.insetter.applyInsetter
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.navigation.AppRouter
import javax.inject.Inject

class MainActivity :
    BaseActivity<MainViewModel, MainState>(R.layout.activity_main, MainViewModel::class.java),
    NavigationContainer<AppRouter> {
    @Inject
    lateinit var flowCicerone: FlowCicerone<AppRouter>
    lateinit var navigator: FlowNavigator<AppRouter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        navigator = FlowNavigator(this, R.id.container, flowCicerone)
        if (savedInstanceState == null) {
            viewModel.startNavigation()
        }
        /*ViewCompat.setWindowInsetsAnimationCallback(
            findViewById<FrameLayout>(R.id.container),
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>,
                ): WindowInsetsCompat {
                    findViewById<FrameLayout>(R.id.container).updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        val insetsTypes = WindowInsetsCompat.Type.ime()
                        updateMargins(
                            bottom = insets.getInsets(insetsTypes).bottom,
                        )
                    }
                    return insets
                }
            })*/
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
