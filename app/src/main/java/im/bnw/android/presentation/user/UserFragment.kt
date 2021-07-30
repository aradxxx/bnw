package im.bnw.android.presentation.user

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.databinding.FragmentUserBinding
import im.bnw.android.databinding.ItemProfileDetailCardBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.LogoutEvent
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.core.view.FailureView
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.REG_DATE
import im.bnw.android.presentation.util.format
import im.bnw.android.presentation.util.networkFailureMessage
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.viewBinding

@Suppress("TooManyFunctions")
class UserFragment : BaseFragment<UserViewModel, UserState>(R.layout.fragment_user) {
    private val binding by viewBinding(FragmentUserBinding::bind)
    override val vmClass = UserViewModel::class.java

    companion object {
        fun newInstance() = UserFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            with(authorized) {
                details.avatar.setOnClickListener { viewModel.avatarClicked() }
                details.messagesCount.detail.setOnClickListener { viewModel.messagesClicked() }
                savedMessagesCount.detail.setOnClickListener { viewModel.savedMessagesClicked() }
                about.donate.setOnClickListener {
                    viewModel.donateClicked(getString(R.string.donate_url))
                }
                logout.setOnClickListener { viewModel.logoutClicked() }
            }
            with(unauthorized) {
                savedMessagesCount.detail.setOnClickListener { viewModel.savedMessagesClicked() }
                about.donate.setOnClickListener {
                    viewModel.donateClicked(getString(R.string.donate_url))
                }
                login.setOnClickListener { viewModel.loginClicked() }
            }
            settings.setOnClickListener { viewModel.settingsClicked() }
            failure.setActionListener { viewModel.retryClicked() }
            swipeToRefresh.setOnRefreshListener { viewModel.retryClicked() }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is LogoutEvent -> logoutConfirmDialog()
            else -> super.onEvent(event)
        }
    }

    override fun onNegativeClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        if (requestCode == DialogCode.LOGOUT_CONFIRM_DIALOG_REQUEST_CODE) {
            viewModel.logoutConfirmed()
        }
    }

    override fun updateState(state: UserState) {
        when (state) {
            is UserState.Loading -> renderLoading()
            is UserState.Failed -> renderFailed(state)
            is UserState.Authorized -> renderAuthorized(state)
            is UserState.Unauthorized -> renderUnauthorized(state)
        }
    }

    private fun renderLoading() = with(binding) {
        swipeToRefresh.isEnabled = false
        failure.isVisible = false
        loadingBar.root.isVisible = true
        settings.isVisible = false
        authorized.root.isVisible = false
        unauthorized.root.isVisible = false
    }

    private fun renderFailed(state: UserState.Failed) = with(binding) {
        swipeToRefresh.isEnabled = false
        failure.showFailure(R.string.no_connection, networkFailureMessage(state.throwable))
        loadingBar.root.isVisible = false
        settings.isVisible = false
        authorized.root.isVisible = false
        unauthorized.root.isVisible = false
    }

    private fun renderAuthorized(state: UserState.Authorized) = with(binding) {
        swipeToRefresh.isEnabled = true
        swipeToRefresh.isRefreshing = false
        failure.isVisible = false
        loadingBar.root.isVisible = false
        settings.isVisible = true
        unauthorized.root.isVisible = false
        with(authorized) {
            root.isVisible = true
            details.userName.newText = state.user.name
            details.firstJoinDate.newText = getString(
                R.string.registered,
                state.user.timestamp().format(REG_DATE)
            )
            details.messagesCount.fillDetail(
                R.drawable.ic_messages,
                R.string.messages_count,
                state.user.messagesCount,
            )
            details.commentsCount.fillDetail(
                R.drawable.ic_comments,
                R.string.comments_count,
                state.user.commentsCount,
            )
            logout.newText = getString(R.string.log_out_from_profile)
            savedMessagesCount.fillDetail(
                R.drawable.ic_save,
                R.string.saved_messages,
                state.savedMessagesCount,
            )
            about.donate.text = getString(R.string.donate)
            Glide.with(requireContext())
                .load(String.format(BuildConfig.USER_AVA_URL, state.user.name))
                .transform(CircleCrop())
                .into(details.avatar)
        }
    }

    private fun renderUnauthorized(state: UserState.Unauthorized) = with(binding) {
        swipeToRefresh.isEnabled = false
        failure.isVisible = false
        loadingBar.root.isVisible = false
        settings.isVisible = true
        authorized.root.isVisible = false
        with(unauthorized) {
            root.isVisible = true
            login.newText = getString(R.string.log_in)
            anonImage.isVisible = true
            savedMessagesCount.fillDetail(
                R.drawable.ic_save,
                R.string.saved_messages,
                state.savedMessagesCount,
            )
            about.donate.text = getString(R.string.donate)
        }
    }

    private fun logoutConfirmDialog() = showDialog {
        NotificationDialog.newInstance(
            null,
            getString(R.string.log_out_confirmation),
            DialogCode.LOGOUT_CONFIRM_DIALOG_REQUEST_CODE,
            getString(R.string.cancel),
            getString(R.string.log_out),
        )
    }

    private fun ItemProfileDetailCardBinding.fillDetail(
        @DrawableRes iconResId: Int,
        @StringRes textResId: Int,
        count: Int,
    ) {
        icon.setImageResource(iconResId)
        text.newText = getString(textResId)
        counter.newText = count.toString()
    }

    private fun FailureView.showFailure(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
    ) {
        title = getString(titleResId)
        message = getString(messageResId)
        isVisible = true
    }
}
