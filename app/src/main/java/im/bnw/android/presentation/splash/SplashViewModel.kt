package im.bnw.android.presentation.splash

import com.github.terrakok.modo.Modo
import im.bnw.android.presentation.core.BaseViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    modo: Modo
) : BaseViewModel<SplashState>(SplashState(), modo)
