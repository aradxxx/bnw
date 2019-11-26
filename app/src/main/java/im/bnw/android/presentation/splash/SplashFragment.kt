package im.bnw.android.presentation.splash

import android.os.Bundle
import android.view.View
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : BaseFragment<SplashViewModel, SplashState>(
    R.layout.fragment_splash,
    SplashViewModel::class.java
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test.setOnClickListener {
            action(Action.TestClick)
        }
    }
}
