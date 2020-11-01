package im.bnw.android.presentation.core.navigation.tab

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import com.github.aradxxx.ciceroneflow.NavigationContainer
import dagger.android.support.DaggerFragment
import im.bnw.android.R
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.Const
import im.bnw.android.presentation.util.tabNavigator
import javax.inject.Inject

class TabFragment : DaggerFragment(R.layout.fragment_tab), NavigationContainer<AppRouter> {
    @Inject
    lateinit var flowCicerone: FlowCicerone<AppRouter>
    private val navigator: FlowNavigator<AppRouter> by lazy {
        tabNavigator(flowCicerone, R.id.tab_container)
    }
    private var tabTag: Int = 0
    private var tabListener: TabListener? = null

    override fun onAttach(context: Context) {
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
        flowCicerone.cicerone(tabTag.toString()).getNavigatorHolder().setNavigator(navigator)
        tabListener?.apply {
            tabChanged(tabTag)
        }
    }

    override fun onPause() {
        flowCicerone.cicerone(tabTag.toString()).getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun router(): AppRouter {
        return flowCicerone.router(tabTag.toString())
    }
}
