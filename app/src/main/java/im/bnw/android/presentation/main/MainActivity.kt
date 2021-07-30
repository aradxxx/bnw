package im.bnw.android.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.android.saveState
import dev.chrisbanes.insetter.applyInsetter
import im.bnw.android.R
import im.bnw.android.databinding.ActivityMainBinding
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.BnwMultiStackFragment
import im.bnw.android.presentation.util.viewBinding
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel, MainState>(
    R.layout.activity_main,
    MainViewModel::class.java,
) {
    @Inject
    lateinit var modo: Modo
    private val modoRender by lazy {
        object : ModoRender(this@MainActivity, R.id.container) {
            override fun createMultiStackFragment() = BnwMultiStackFragment()
        }
    }
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modo.init(savedInstanceState, Screens.tabs())
        checkDeepLink(intent)
        binding.container.applyInsetter {
            type(ime = true) {
                padding(animated = true)
            }
        }
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
