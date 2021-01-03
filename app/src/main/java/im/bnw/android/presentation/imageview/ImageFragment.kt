package im.bnw.android.presentation.imageview

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import im.bnw.android.R
import im.bnw.android.databinding.FragmentImageViewBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments

class ImageFragment : BaseFragment<ImageViewModel, ImageState>(
    R.layout.fragment_image_view
) {
    override val vmClass = ImageViewModel::class.java
    private val binding by viewBinding(FragmentImageViewBinding::bind)

    companion object {
        fun newInstance(params: ImageScreenParams) = ImageFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDecorFits(false)
        binding.btnClose.setOnClickListener { viewModel.backPressed() }
    }

    override fun onDestroyView() {
        setDecorFits(true)
        super.onDestroyView()
    }

    override fun updateState(state: ImageState) {
        Glide.with(requireContext())
            .load(state.url)
            .into(binding.imageView)
    }

    private fun setDecorFits(decorFits: Boolean) {
        val window = activity?.window
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, decorFits)
        }
    }
}
