package im.bnw.android.presentation.imageview

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import javax.inject.Inject

class ImageViewModel @Inject constructor(
    router: AppRouter,
    restoredState: ImageState?,
    screenParams: ImageScreenParams,
) : BaseViewModel<ImageState>(
    restoredState ?: ImageState(fullUrl = screenParams.fullUrl),
    router
)
