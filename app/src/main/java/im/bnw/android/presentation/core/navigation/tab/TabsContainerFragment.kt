package im.bnw.android.presentation.core.navigation.tab

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.AndroidSupportInjection
import im.bnw.android.R
import im.bnw.android.databinding.FragmentTabsContainerBinding
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.util.tabNavigator
import im.bnw.android.presentation.util.viewBinding
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
    private val binding by viewBinding(FragmentTabsContainerBinding::bind)

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        bnwSilently { router().switchTab(Tab.from(menuItem.itemId).screen()) }
        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) {
        router().backToRoot(Tab.from(menuItem.itemId))
    }

    override fun tabChanged(tag: Int) {
        bnwSilently { binding.bottomNavigationView.selectedItemId = tag }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        tabCicerone.tabsContainerCicerone().navigatorHolder.setNavigator(navigator)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener(this)
    }

    override fun onPause() {
        tabCicerone.tabsContainerCicerone().navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun router(): AppRouter {
        return tabCicerone.tabsContainerRouter()
    }

    private fun bnwSilently(action: () -> Unit) {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(null)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener(null)
        action()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener(this)
    }
}
