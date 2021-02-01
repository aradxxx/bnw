package im.bnw.android.presentation.core.navigation.tab

import android.view.MenuItem
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import com.github.aradxxx.ciceroneflow.NavigationContainer
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerFragment
import im.bnw.android.R
import im.bnw.android.databinding.FragmentTabsContainerBinding
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.tabNavigator
import im.bnw.android.presentation.util.viewBinding
import javax.inject.Inject

class TabsContainerFragment :
    DaggerFragment(R.layout.fragment_tabs_container),
    BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener,
    NavigationContainer<AppRouter>,
    TabListener {
    @Inject
    lateinit var flowCicerone: FlowCicerone<AppRouter>
    private val navigator: FlowNavigator<AppRouter> by lazy {
        tabNavigator(flowCicerone, R.id.tabs_container)
    }
    private val binding by viewBinding(FragmentTabsContainerBinding::bind)

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        bnwSilently { router().switchTab(Tab.from(menuItem.itemId)) }
        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) {
        router().backToRoot(Tab.from(menuItem.itemId))
    }

    override fun tabChanged(tag: Int) {
        bnwSilently { binding.tabsNavigation.selectedItemId = tag }
    }

    override fun onResume() {
        super.onResume()
        flowCicerone.flowContainerCicerone().getNavigatorHolder().setNavigator(navigator)
        with(binding) {
            tabsNavigation.setOnNavigationItemSelectedListener(this@TabsContainerFragment)
            tabsNavigation.setOnNavigationItemReselectedListener(this@TabsContainerFragment)
        }
    }

    override fun onPause() {
        flowCicerone.flowContainerCicerone().getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun router(): AppRouter {
        return flowCicerone.flowContainerRouter()
    }

    private fun bnwSilently(action: () -> Unit) {
        with(binding) {
            tabsNavigation.setOnNavigationItemSelectedListener(null)
            tabsNavigation.setOnNavigationItemReselectedListener(null)
            action()
            tabsNavigation.setOnNavigationItemSelectedListener(this@TabsContainerFragment)
            tabsNavigation.setOnNavigationItemReselectedListener(this@TabsContainerFragment)
        }
    }
}
