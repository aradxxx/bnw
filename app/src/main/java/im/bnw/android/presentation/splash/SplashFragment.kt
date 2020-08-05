package im.bnw.android.presentation.splash

import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment

class SplashFragment : BaseFragment<SplashViewModel, SplashState>(
    R.layout.fragment_splash
) {
    override val vmClass = SplashViewModel::class.java
}
