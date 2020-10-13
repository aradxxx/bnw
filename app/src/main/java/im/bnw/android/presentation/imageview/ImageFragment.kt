package im.bnw.android.presentation.imageview

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.util.withInitialArguments
import kotlinx.android.synthetic.main.fragment_image_view.*

class ImageFragment : BaseFragment<ImageViewModel, ImageState>(
    R.layout.fragment_image_view
) {
    companion object {
        fun newInstance(params: ImageScreenParams) =
            ImageFragment().withInitialArguments(params)
    }

    override val vmClass = ImageViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDecorFits(false)
        btn_close.setOnClickListener { viewModel.backPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setDecorFits(true)
    }

    override fun updateState(state: ImageState) {
        Glide.with(requireContext())
            .load(state.fullUrl)
            .into(image_view)
    }

    private fun setDecorFits(decorFits: Boolean) {
        val window = activity?.window
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, decorFits)
        }
    }
}
