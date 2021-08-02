package im.bnw.android.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.android.saveState
import im.bnw.android.R
import im.bnw.android.databinding.ActivityMainBinding
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.BnwMultiStackFragment
import im.bnw.android.presentation.util.viewBinding
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel, MainState>(
    MainViewModel::class.java,
) {
    @Inject
    lateinit var modo: Modo
    private val modoRender by lazy {
        object : ModoRender(this@MainActivity, R.id.container) {
            override fun createMultiStackFragment() = BnwMultiStackFragment()
            override fun setupTransaction(
                fragmentManager: FragmentManager,
                transaction: FragmentTransaction,
                screen: AppScreen,
                newFragment: Fragment
            ) {
                if (!transitionAnimations) {
                    return
                }
                transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                )
            }
        }
    }
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val insetListener = OnApplyWindowInsetsListener { _, insets ->
        binding.container.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            val insetsTypes = WindowInsetsCompat.Type.statusBars()
            updateMargins(
                top = insets.getInsets(insetsTypes).top,
            )
        }
        insets
    }
    private val insetAnimationCallback = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
        override fun onProgress(
            insets: WindowInsetsCompat,
            runningAnimations: MutableList<WindowInsetsAnimationCompat>,
        ): WindowInsetsCompat {
            binding.container.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                val insetsTypes = WindowInsetsCompat.Type.ime()
                updateMargins(
                    bottom = insets.getInsets(insetsTypes).bottom,
                )
            }
            return insets
        }
    }
    private var transitionAnimations = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        modo.init(savedInstanceState, Screens.tabs())
        checkDeepLink(intent)
        ViewCompat.setOnApplyWindowInsetsListener(binding.container, insetListener)
        ViewCompat.setWindowInsetsAnimationCallback(binding.container, insetAnimationCallback)
    }

    override fun onResume() {
        super.onResume()
        modo.render = modoRender
    }

    override fun onPause() {
        modo.render = null
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkDeepLink(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        modo.saveState(outState)
    }

    override fun updateState(state: MainState) = when (state) {
        MainState.Init -> {
            // no op
        }
        is MainState.Main -> {
            transitionAnimations = state.transitionAnimations
            renderMain(state)
        }
    }

    private fun renderMain(state: MainState.Main) {
        AppCompatDelegate.setDefaultNightMode(state.theme.toDelegateTheme())
    }

    private fun checkDeepLink(intent: Intent?) {
        intent ?: return
        viewModel.checkDeepLink(intent)
        setIntent(intent.setData(null))
    }

    private fun ThemeSettings.toDelegateTheme(): Int = when (this) {
        ThemeSettings.Default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ThemeSettings.Light -> AppCompatDelegate.MODE_NIGHT_NO
        ThemeSettings.Dark -> AppCompatDelegate.MODE_NIGHT_YES
    }
}
