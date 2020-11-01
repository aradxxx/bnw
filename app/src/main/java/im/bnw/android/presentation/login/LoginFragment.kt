package im.bnw.android.presentation.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.databinding.FragmentLoginBinding
import im.bnw.android.presentation.core.BaseFragment
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

        binding.login.doAfterTextChanged { viewModel.userNameChanged(it.toString()) }
        binding.password.doAfterTextChanged { viewModel.passwordChanged(it.toString()) }
        binding.auth.setOnClickListener { viewModel.onAuthClicked() }
    }

    override fun updateState(state: LoginState) {
        if (binding.login.text.toString() != state.userName) {
            binding.login.setText(state.userName)
        }
        if (binding.password.text.toString() != state.password) {
            binding.password.setText(state.password)
        }
        binding.login.isEnabled = !state.loading
        binding.password.isEnabled = !state.loading
        binding.auth.isEnabled =
            !state.loading && (state.userName.isNotEmpty() && state.password.isNotEmpty())
    }
}
