package im.bnw.android.presentation.main

import im.bnw.android.presentation.core.BaseViewModel
import im.bnw.android.presentation.core.navigation.AppRouter
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val restoredState: MainState?,
    private val router: AppRouter
) : BaseViewModel<MainState>(
    restoredState ?: MainState()
)