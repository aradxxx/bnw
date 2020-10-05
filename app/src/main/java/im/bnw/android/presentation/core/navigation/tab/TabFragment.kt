package im.bnw.android.presentation.core.navigation.tab

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import im.bnw.android.R
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.Const
import im.bnw.android.presentation.util.tabNavigator
import ru.aradxxx.ciceronetabs.NavigationContainer
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator
import ru.aradxxx.ciceronetabs.TabRouter
import ru.terrakok.cicerone.Cicerone
import javax.inject.Inject

class TabFragment : Fragment(R.layout.fragment_tab), NavigationContainer<TabRouter> {
    @Inject
    lateinit var tabCicerone: TabCicerone<AppRouter>
    private val navigator: TabNavigator<AppRouter> by lazy {
        tabNavigator(tabCicerone, R.id.tab_container)
    }
    private var tabTag: Int = 0
    private var tabListener: TabListener? = null

    override fun onAttach(context: Context) {
        //AndroidXInjection.inject(this)
        super.onAttach(context)
        if (parentFragment is TabListener) {
            tabListener = parentFragment as TabListener
        }
    }

    companion object {
        fun newInstance(tag: Int) = TabFragment().apply {
            arguments = bundleOf(Const.BUNDLE_INITIAL_ARGS to tag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabTag = requireArguments().getInt(Const.BUNDLE_INITIAL_ARGS)
    }

    override fun onResume() {
        super.onResume()
        cicerone().navigatorHolder.setNavigator(navigator)
        tabListener?.apply {
            tabChanged(tabTag)
        }
    }

    override fun onPause() {
        cicerone().navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun cicerone(): Cicerone<AppRouter> {
        return tabCicerone.cicerone(tabTag.toString())
    }

    override fun router(): TabRouter = cicerone().router
}
