package im.bnw.android.presentation.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.presentation.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment<LoginViewModel, LoginState>(
    R.layout.fragment_login
) {
    companion object {
        fun newInstance() = LoginFragment()
    }

    override val vmClass = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login.doAfterTextChanged { viewModel.userNameChanged(it.toString()) }
        password.doAfterTextChanged { viewModel.passwordChanged(it.toString()) }
        auth.setOnClickListener { viewModel.onAuthClicked() }
    }

    override fun updateState(state: LoginState) {
        if (login.text.toString() != state.userName) {
            login.setText(state.userName)
        }
        if (password.text.toString() != state.password) {
            password.setText(state.password)
        }
        login.isEnabled = !state.loading
        password.isEnabled = !state.loading
        auth.isEnabled =
            !state.loading && (state.userName.isNotEmpty() && state.password.isNotEmpty())
    }
}
