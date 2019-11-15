package im.bnw.android.presentation.core.tab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import im.bnw.android.R
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import kotlinx.android.synthetic.main.fragment_tabs_container.*
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator
import javax.inject.Inject


class TabsContainerFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        bottom_navigation_view.setOnNavigationItemSelectedListener(null)
        bottom_navigation_view.setOnNavigationItemReselectedListener(null)
        router().switchTab(TabScreen(Tab.from(menuItem.itemId).tag()))
        bottom_navigation_view.setOnNavigationItemSelectedListener(this)
        bottom_navigation_view.setOnNavigationItemReselectedListener(this)
        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) {
        router().backTo(Tab.from(menuItem.itemId).tag(), null)
    }

    @Inject
    lateinit var tabCicerone: TabCicerone<AppRouter>
    private var navigator: TabNavigator<AppRouter>? = null

    override fun onAttach(context: Context) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tabs_container, container, false)
    }

    override fun onResume() {
        super.onResume()
        tabCicerone.tabsContainerCicerone().navigatorHolder.setNavigator(navigator())
        bottom_navigation_view.setOnNavigationItemSelectedListener(this)
        bottom_navigation_view.setOnNavigationItemReselectedListener(this)
    }

    override fun onPause() {
        tabCicerone.tabsContainerCicerone().navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun navigator(): TabNavigator<AppRouter> {
        val activity: MainActivity = requireActivity() as MainActivity
        if (navigator == null) {
            navigator = TabNavigator(
                activity, tabCicerone, childFragmentManager,
                R.id.tabs_container
            )
        }
        return navigator!!
    }

    private fun router(): AppRouter {
        return tabCicerone.tabsContainerRouter()
    }
}

