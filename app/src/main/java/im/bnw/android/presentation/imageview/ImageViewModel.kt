package im.bnw.android.presentation.imageview

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.core.navigation.Screens
import im.bnw.android.presentation.core.navigation.tab.Tab
import im.bnw.android.presentation.messages.MessagesScreenParams
import javax.inject.Inject

class ImageViewModel @Inject constructor(
    router: AppRouter,
    imageParams: ImageScreenParams,
) : BaseViewModel<ImageState>(ImageState(), router)
