package im.bnw.android.presentation.login

import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment

class LoginFragment : BaseFragment<LoginViewModel, LoginState>(
    R.layout.fragment_login
) {
    override val vmClass = LoginViewModel::class.java

    companion object {
        fun newInstance() = LoginFragment()
    }
}
