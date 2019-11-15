package im.bnw.android.presentation.core.tab

import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class TabScreen(private val tag: String) : SupportAppScreen() {
    override fun getScreenKey(): String {
        return tag
    }

    override fun getFragment(): Fragment {
        return TabFragment.newInstance(tag)
    }
}
