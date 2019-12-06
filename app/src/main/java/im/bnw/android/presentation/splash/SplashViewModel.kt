package im.bnw.android.presentation.splash

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    router: AppRouter
) : BaseViewModel<SplashState>(SplashState(), router)
