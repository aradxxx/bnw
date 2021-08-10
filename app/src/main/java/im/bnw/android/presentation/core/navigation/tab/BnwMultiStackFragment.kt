package im.bnw.android.presentation.core.navigation.tab

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.NavigationRender
import com.github.terrakok.modo.android.MultiStackFragment
import com.github.terrakok.modo.selectStack
import dagger.android.support.AndroidSupportInjection
import im.bnw.android.R
import im.bnw.android.presentation.util.attrColor
import im.bnw.android.presentation.util.dpToPxF
import javax.inject.Inject

private const val BOTTOM_NAVIGATION_ELEVATION = 8

class BnwMultiStackFragment : MultiStackFragment() {
    @Inject
    lateinit var modo: Modo

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addElevationToBottomNavigation(view)
    }

    private fun createTabView(index: Int, parent: LinearLayout): View {
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

    private var multiScreen: MultiScreen? = null
        set(value) {
            if (value != null) {
                if (field == null) {
                    view?.findViewById<LinearLayout>(TAB_CONTAINER_ID)?.let {
                        createTabs(value, it)
                    }
                }

                field = value
                localRenders.forEach { (index, render) ->
                    value.stacks.getOrNull(index)?.let { state ->
                        render(state)
                    }
                }
                selectTab(value.selectedStack)
            }
        }

    private val localRenders = mutableMapOf<Int, NavigationRender>()
    internal fun setRender(index: Int, render: NavigationRender?) {
        if (render != null) {
            localRenders[index] = render
            multiScreen?.stacks?.getOrNull(index)?.let { state ->
                render(state)
            }
        } else {
            localRenders.remove(index)
        }
    }

    override fun applyMultiState(multiScreen: MultiScreen) {
        this.multiScreen = multiScreen
    }

    override fun getCurrentFragment(): Fragment? =
        childFragmentManager.fragments
            .filterIsInstance<BnwStackContainerFragment>()
            .firstOrNull { it.isVisible }
            ?.getCurrentFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LinearLayout(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(container!!.layoutParams).apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
            orientation = LinearLayout.VERTICAL
        }

        val linearLayout = this
        val frame = FrameLayout(requireContext()).apply {
            id = CONTAINER_ID
            layoutParams = LinearLayout.LayoutParams(linearLayout.layoutParams).apply {
                height = 0
                width = ViewGroup.LayoutParams.MATCH_PARENT
                weight = 1F
            }
        }
        addView(frame)

        val tabContainer = LinearLayout(requireContext()).apply {
            id = TAB_CONTAINER_ID
            layoutParams = LinearLayout.LayoutParams(linearLayout.layoutParams).apply {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        addView(tabContainer)
        multiScreen?.let { createTabs(it, tabContainer) }
    }

    private fun createTabs(state: MultiScreen, container: LinearLayout) {
        container.removeAllViews()
        for (i in state.stacks.indices) {
            createTabView(i, container).apply {
                layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
                    width = 0
                    weight = 1F
                }
                isSelected = i == state.selectedStack
                container.addView(this)
            }
        }
    }

    private fun selectTab(index: Int) {
        view?.findViewById<LinearLayout>(TAB_CONTAINER_ID)?.forEachIndexed { i, child ->
            child.isSelected = i == index
        }

        val addedFragments = childFragmentManager.fragments
            .filterIsInstance<BnwStackContainerFragment>()
        val currentContainerFragment = addedFragments
            .firstOrNull { it.isVisible }

        if (currentContainerFragment?.index == index) return

        childFragmentManager.beginTransaction().also { transaction ->
            val tabExists = childFragmentManager.findFragmentByTag(index.toString()) != null
            if (!tabExists) {
                transaction.add(
                    CONTAINER_ID,
                    BnwStackContainerFragment.create(index),
                    index.toString()
                )
            }
            for (i in 0 until (multiScreen?.stacks?.count() ?: 0)) {
                val f = childFragmentManager
                    .findFragmentByTag(i.toString()) as? BnwStackContainerFragment ?: continue
                if (f.index == index && !f.isVisible) {
                    transaction.attach(f)
                } else if (f.index != index && f.isVisible) {
                    transaction.detach(f)
                }
            }
        }.commitNowAllowingStateLoss()
    }

    companion object {
        private const val CONTAINER_ID = 9283
        private const val TAB_CONTAINER_ID = 9284
    }
}
