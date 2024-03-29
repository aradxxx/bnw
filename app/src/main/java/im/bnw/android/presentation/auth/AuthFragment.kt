package im.bnw.android.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import im.bnw.android.R
import im.bnw.android.databinding.FragmentAuthBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.util.hideKeyboard
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.showKeyboard
import im.bnw.android.presentation.util.viewBinding

class AuthFragment : BaseFragment<AuthViewModel, AuthState>(R.layout.fragment_auth) {
    private val binding by viewBinding(FragmentAuthBinding::bind)
    override val vmClass = AuthViewModel::class.java

    companion object {
        fun newInstance() = AuthFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.setNavigationOnClickListener {
                viewModel.backPressed()
            }
            login.doAfterTextChanged {
                viewModel.loginChanged(it.toString())
            }
            password.doAfterTextChanged {
                viewModel.passwordChanged(it.toString())
            }
            cancel.setOnClickListener {
                viewModel.backPressed()
            }
            signIn.setOnClickListener {
                viewModel.signInClicked()
            }
            showKeyboard(login)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun updateState(state: AuthState) {
        if (state.loading) {
            renderLoadingState()
        } else {
            renderIdleState(state)
        }
    }

    private fun renderIdleState(state: AuthState) = with(binding) {
        loginLayout.isVisible = true
        loginLayout.isEnabled = true
        login.newText = state.userName
        passwordLayout.isVisible = true
        passwordLayout.isEnabled = true
        password.newText = state.password
        cancel.isVisible = true
        signIn.isVisible = true
        signIn.isEnabled = state.userName.isNotEmpty() && state.password.isNotEmpty()
        progressBar.isVisible = false
    }

    private fun renderLoadingState() = with(binding) {
        loginLayout.isVisible = false
        passwordLayout.isVisible = false
        cancel.isVisible = false
        signIn.isVisible = false
        progressBar.isVisible = true
    }
}
