package im.bnw.android.presentation.core.navigation.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.android.ModoRender

class BnwStackContainerFragment : Fragment() {
    val index by lazy { requireArguments().getInt(ARG_INDEX) }

    private val render by lazy {
        ModoRender(childFragmentManager, CONTAINER_ID) {
            // do nothing
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FrameLayout(requireContext()).apply {
        id = CONTAINER_ID
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as? BnwMultiStackFragment)?.setRender(index, render)
    }

    override fun onPause() {
        (parentFragment as? BnwMultiStackFragment)?.setRender(index, null)
        super.onPause()
    }

    fun getCurrentFragment(): Fragment? =
        childFragmentManager.findFragmentById(CONTAINER_ID)

    companion object {
        private const val CONTAINER_ID = 2387
        private const val ARG_INDEX = "arg_index"

        fun create(index: Int) = BnwStackContainerFragment().apply {
            arguments = bundleOf(ARG_INDEX to index)
        }
    }
}
