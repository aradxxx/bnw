package im.bnw.android.presentation.core.navigation.tab

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.github.terrakok.modo.android.multi.MultiStackFragmentImpl
import com.github.terrakok.modo.selectStack
import im.bnw.android.App
import im.bnw.android.R
import im.bnw.android.presentation.util.attrColor

class BnwMultiStackFragment : MultiStackFragmentImpl() {
    private val modo = App.modo
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
}
