package im.bnw.android.presentation.core.navigation.tab

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import im.bnw.android.R
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.tabNavigator
import kotlinx.android.synthetic.main.fragment_tabs_container.*
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator
import javax.inject.Inject

class TabsContainerFragment :
    Fragment(R.layout.fragment_tabs_container),
    BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener,
    TabListener {
    @Inject
    lateinit var tabCicerone: TabCicerone<AppRouter>
    private val navigator: TabNavigator<AppRouter> by lazy {
        tabNavigator(tabCicerone, R.id.tabs_container)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        bnwSilently { router().switchTab(Tab.from(menuItem.itemId).screen()) }
        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) {
        router().backToRoot(Tab.from(menuItem.itemId))
    }

    override fun tabChanged(tag: Int) {
        bnwSilently { bottom_navigation_view.selectedItemId = tag }
    }

    override fun onAttach(context: Context) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        tabCicerone.tabsContainerCicerone().navigatorHolder.setNavigator(navigator)
        bottom_navigation_view.setOnNavigationItemSelectedListener(this)
        bottom_navigation_view.setOnNavigationItemReselectedListener(this)
    }

    override fun onPause() {
        tabCicerone.tabsContainerCicerone().navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun router(): AppRouter {
        return tabCicerone.tabsContainerRouter()
    }

    private fun bnwSilently(action: () -> Unit) {
        bottom_navigation_view.setOnNavigationItemSelectedListener(null)
        bottom_navigation_view.setOnNavigationItemReselectedListener(null)
        action()
        bottom_navigation_view.setOnNavigationItemSelectedListener(this)
        bottom_navigation_view.setOnNavigationItemReselectedListener(this)
    }
}
