package im.bnw.android.presentation.main

import android.content.Intent
import android.os.Bundle
import com.github.terrakok.modo.android.ModoRender
import com.github.terrakok.modo.android.init
import com.github.terrakok.modo.android.saveState
import im.bnw.android.App
import im.bnw.android.R
import im.bnw.android.databinding.ActivityMainBinding
import im.bnw.android.presentation.core.BaseActivity
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.BnwMultiStackFragment
import im.bnw.android.presentation.util.viewBinding

class MainActivity : BaseActivity<MainViewModel, MainState>(R.layout.activity_main, MainViewModel::class.java) {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val modo = App.modo
    private val modoRender by lazy {
        object : ModoRender(this@MainActivity, R.id.container) {
            override fun createMultiStackFragment() = BnwMultiStackFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modo.init(savedInstanceState, Screens.tabs())
        checkDeepLink(intent)
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

    private fun checkDeepLink(intent: Intent?) {
        intent ?: return
        viewModel.checkDeepLink(intent)
        setIntent(intent.setData(null))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        modo.saveState(outState)
    }
}
