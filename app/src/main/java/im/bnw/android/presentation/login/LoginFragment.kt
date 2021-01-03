package im.bnw.android.presentation.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.databinding.FragmentLoginBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.viewBinding

class LoginFragment : BaseFragment<LoginViewModel, LoginState>(
    R.layout.fragment_login
) {
    override val vmClass = LoginViewModel::class.java
    private val binding by viewBinding(FragmentLoginBinding::bind)

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            login.doAfterTextChanged { viewModel.userNameChanged(it.toString()) }
            password.doAfterTextChanged { viewModel.passwordChanged(it.toString()) }
            auth.setOnClickListener { viewModel.onAuthClicked() }
        }
    }

    override fun updateState(state: LoginState) {
        with(binding) {
            login.newText = state.userName
            password.newText = state.password
            login.isEnabled = !state.loading
            password.isEnabled = !state.loading
            auth.isEnabled = !state.loading && (state.userName.isNotEmpty() && state.password.isNotEmpty())
        }
    }
}
