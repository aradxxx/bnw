package im.bnw.android.presentation.core.tab

import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class TabsContainerScreen : SupportAppScreen() {
    override fun getFragment(): Fragment {
        return TabsContainerFragment()
    }
}