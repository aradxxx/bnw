package im.bnw.android.presentation.core.navigation.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.forEach
import com.github.terrakok.modo.android.multi.MultiStackFragmentImpl
import com.github.terrakok.modo.selectStack
import im.bnw.android.App
import im.bnw.android.R
import im.bnw.android.presentation.util.attrColor
import im.bnw.android.presentation.util.dpToPxF

private const val BOTTOM_NAVIGATION_ELEVATION = 8

class BnwMultiStackFragment : MultiStackFragmentImpl() {
    private val modo = App.modo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addElevationToBottomNavigation(view)
    }

    override fun createTabView(index: Int, parent: LinearLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.view_tab, parent, false).apply {
            parent.setBackgroundColor(context.attrColor(R.attr.barColor))
            val tab: Tab = Tab.values()[index]
            val image = findViewById<ImageView>(R.id.icon)
            image.apply {
                setImageResource(tab.icon)
                contentDescription = getString(tab.title)
            }
            setOnClickListener {
                modo.selectStack(index)
            }
            setOnLongClickListener {
                Toast.makeText(context, tab.title, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun addElevationToBottomNavigation(view: View) {
        if (view is ViewGroup) {
            view.forEach {
                if (it is LinearLayout) {
                    it.elevation = BOTTOM_NAVIGATION_ELEVATION.dpToPxF
                    return@forEach
                }
            }
        }
    }
}
