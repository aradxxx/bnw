package im.bnw.android.presentation.user

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.switchmaterial.SwitchMaterial
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.databinding.FragmentUserBinding
import im.bnw.android.databinding.IncludeProfileDetailsBinding
import im.bnw.android.databinding.IncludeSettingsBinding
import im.bnw.android.databinding.ItemProfileDetailCardBinding
import im.bnw.android.databinding.ItemProfileSettingCardBinding
import im.bnw.android.domain.settings.Settings
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.core.view.FailureView
import im.bnw.android.presentation.user.adapter.SettingsDialog
import im.bnw.android.presentation.user.adapter.SettingsItem
import im.bnw.android.presentation.util.Const.ARGUMENT_SETTING
import im.bnw.android.presentation.util.Const.SETTINGS_REQUEST_KEY
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.setLocale
import im.bnw.android.presentation.util.timeAgoString
import im.bnw.android.presentation.util.toItem
import im.bnw.android.presentation.util.toSetting
import im.bnw.android.presentation.util.viewBinding
import javax.net.ssl.SSLException

@SuppressWarnings("TooManyFunctions")
class UserFragment : BaseFragment<UserViewModel, UserState>(
    R.layout.fragment_user
) {
    private val binding by viewBinding(FragmentUserBinding::bind)
    override val vmClass = UserViewModel::class.java

    companion object {
        fun newInstance() = UserFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            login.setOnClickListener { viewModel.loginClicked() }
            failure.setActionListener { viewModel.retryClicked() }
            details.messagesCount.detail.setOnClickListener { viewModel.messagesClicked() }
            details.avatar.setOnClickListener {
                viewModel.avatarClicked()
            }
            anonymity.setOnCheckedChangeListener { _, checked ->
                viewModel.anonymityClicked(checked)
            }
            scrollToReplies.setOnCheckedChangeListener { _, isChecked ->
                viewModel.scrollToRepliesChanged(isChecked)
            }

            settings.theme.setting.setOnClickListener { viewModel.chooseTheme() }
            settings.language.setting.setOnClickListener { viewModel.chooseLanguage() }
            toolbar.setOnMenuItemClickListener { item -> menuItemClicked(item) }
            setFragmentResultListener(SETTINGS_REQUEST_KEY) { _, bundle ->
                when (val setting = bundle.getParcelable<SettingsItem>(ARGUMENT_SETTING)) {
                    is ThemeItem -> viewModel.themeChanged(setting.toSetting())
                    is LanguageItem -> {
                        val language = setting.toSetting()
                        viewModel.languageChanged(language)
                        language.setLocale(requireContext())
                    }
                    null -> throw IllegalArgumentException("Unknown setting class")
                }
            }
        }
    }

    override fun onEvent(event: Any?) {
        when (event) {
            is SettingsDialogEvent -> {
                showDialog(SettingsDialog.SETTINGS_DIALOG_TAG) {
                    SettingsDialog.newInstance(
                        getString(event.title),
                        event.currentItem,
                        event.items,
                    )
                }
            }
            else -> super.onEvent(event)
        }
    }

    override fun onNegativeClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        if (requestCode == DialogCode.LOGOUT_CONFIRM_DIALOG_REQUEST_CODE) {
            viewModel.logoutConfirmed()
        }
    }

    override fun updateState(state: UserState) = when (state) {
        is UserState.Init -> renderInitState()
        is UserState.Loading -> renderLoadingState()
        is UserState.LoadingFailed -> renderLoadingFailedState(state)
        is UserState.Unauthorized -> renderUnauthorizedState(state)
        is UserState.UserInfo -> renderProfileInfo(state)
    }

    private fun renderInitState() = with(binding) {
        appBar.isVisible = false
        details.drawDetails(null)
        anonymity.isVisible = false
        scrollToReplies.isVisible = false
        settings.drawSettings(null)
        login.isVisible = false
        failure.isVisible = false
        progressBar.isVisible = false
        anonImage.isVisible = false
    }

    private fun renderLoadingState() = with(binding) {
        appBar.isVisible = false
        details.drawDetails(null)
        anonymity.isVisible = false
        scrollToReplies.isVisible = false
        settings.drawSettings(null)
        login.isVisible = false
        failure.isVisible = false
        progressBar.isVisible = true
        anonImage.isVisible = false
    }

    private fun renderLoadingFailedState(state: UserState.LoadingFailed) = with(binding) {
        val messageResId = if (state.throwable is SSLException) {
            R.string.possibly_domain_blocked
        } else {
            R.string.check_connection_and_tap_retry
        }
        appBar.isVisible = false
        details.drawDetails(null)
        anonymity.isVisible = false
        scrollToReplies.isVisible = false
        settings.drawSettings(null)
        login.isVisible = false
        failure.showFailure(R.string.no_connection, messageResId)
        progressBar.isVisible = false
        anonImage.isVisible = false
    }

    private fun renderUnauthorizedState(state: UserState.Unauthorized) = with(binding) {
        appBar.isVisible = false
        details.drawDetails(null)
        anonymity.isVisible = false
        scrollToReplies.drawSwitcher(state.settings.scrollToReplies, R.string.scroll_to_replies)
        settings.drawSettings(state.settings)
        login.isVisible = true
        failure.isVisible = false
        progressBar.isVisible = false
        anonImage.isVisible = true
    }

    private fun renderProfileInfo(state: UserState.UserInfo) = with(binding) {
        appBar.isVisible = true
        toolbar.title = state.user.name
        details.drawDetails(state.user)
        anonymity.drawSwitcher(state.settings.incognito, R.string.anonymity)
        scrollToReplies.drawSwitcher(state.settings.scrollToReplies, R.string.scroll_to_replies)
        settings.drawSettings(state.settings)
        login.isVisible = false
        failure.isVisible = false
        progressBar.isVisible = false
        anonImage.isVisible = false
    }

    private fun IncludeProfileDetailsBinding.drawDetails(user: User?) {
        if (user == null) {
            detailCard.isVisible = false
            return
        }
        detailCard.isVisible = true
        firstJoinDate.newText = timeAgoString(requireContext(), user.timestamp())
        messagesCount.fillDetail(
            R.drawable.ic_messages,
            R.string.messages_count,
            user.messagesCount,
        )
        commentsCount.fillDetail(
            R.drawable.ic_comments,
            R.string.comments_count,
            user.commentsCount,
        )
        Glide.with(requireContext())
            .load(String.format(BuildConfig.USER_AVA_URL, user.name))
            .transform(CircleCrop())
            .into(avatar)
    }

    private fun SwitchMaterial.drawSwitcher(
        checked: Boolean,
        @StringRes nameResId: Int
    ) {
        isVisible = true
        isChecked = checked
        newText = getString(nameResId)
    }

    private fun IncludeSettingsBinding.drawSettings(settings: Settings?) {
        if (settings == null) {
            settingsCard.isVisible = false
            return
        }
        settingsCard.isVisible = true
        theme.fillSetting(R.string.theme, settings.theme.toItem().nameResId)
        language.fillSetting(R.string.language, settings.language.toItem().nameResId)
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

    private fun ItemProfileSettingCardBinding.fillSetting(
        @StringRes nameResId: Int,
        @StringRes valueResId: Int,
    ) {
        name.newText = getString(nameResId)
        value.newText = getString(valueResId)
    }

    private fun FailureView.showFailure(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
    ) {
        isVisible = true
        title = getString(titleResId)
        message = getString(messageResId)
    }

    private fun menuItemClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_logout -> {
                logoutConfirmDialog()
                true
            }
            else -> false
        }
    }

    private fun logoutConfirmDialog() {
        showDialog {
            NotificationDialog.newInstance(
                null,
                getString(R.string.log_out_confirmation),
                DialogCode.LOGOUT_CONFIRM_DIALOG_REQUEST_CODE,
                getString(R.string.cancel),
                getString(R.string.log_out),
            )
        }
    }
}
