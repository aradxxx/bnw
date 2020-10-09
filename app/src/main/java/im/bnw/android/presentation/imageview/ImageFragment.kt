package im.bnw.android.presentation.imageview

import android.os.Bundle
import android.view.View
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.messages.MessagesFragment
import im.bnw.android.presentation.messages.MessagesScreenParams
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

        //image_view.setImageResource()
    }
}
