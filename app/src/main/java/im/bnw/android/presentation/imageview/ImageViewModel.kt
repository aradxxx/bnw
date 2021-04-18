package im.bnw.android.presentation.imageview

import im.bnw.android.presentation.core.BaseViewModel
import javax.inject.Inject

class ImageViewModel @Inject constructor(
    restoredState: ImageState?,
    screenParams: ImageScreenParams,
) : BaseViewModel<ImageState>(
    restoredState ?: ImageState(url = screenParams.url)
)
