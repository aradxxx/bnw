package im.bnw.android.presentation.core.tab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import im.bnw.android.R
import im.bnw.android.di.core.AndroidXInjection
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import ru.aradxxx.ciceronetabs.TabCicerone
import ru.aradxxx.ciceronetabs.TabNavigator
import ru.terrakok.cicerone.Cicerone
import javax.inject.Inject

class TabFragment : Fragment() {
    @Inject
    lateinit var tabCicerone: TabCicerone<AppRouter>
    private var navigator: TabNavigator<AppRouter>? = null
    private lateinit var tabTag: String

    override fun onAttach(context: Context) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        const val BUNDLE_TAG = "BUNDLE_TAG"
        fun newInstance(tag: String): TabFragment {
            val bundle = Bundle()
            bundle.putString(BUNDLE_TAG, tag)
            val fragment = TabFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabTag = arguments?.getString(BUNDLE_TAG)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onResume() {
        super.onResume()
        cicerone().navigatorHolder.setNavigator(navigator())
    }

    override fun onPause() {
        cicerone().navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun navigator(): TabNavigator<AppRouter> {
        val activity: MainActivity = requireActivity() as MainActivity
        if (navigator == null) {
            navigator = TabNavigator(
                activity, tabCicerone, childFragmentManager,
                R.id.tab_container
            )
        }
        return navigator!!
    }

    private fun cicerone(): Cicerone<AppRouter> {
        return tabCicerone.cicerone(tabTag)
    }
}