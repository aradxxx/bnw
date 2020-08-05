package im.bnw.android.presentation.login

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    router: AppRouter
) : BaseViewModel<LoginState>(LoginState(), router)
