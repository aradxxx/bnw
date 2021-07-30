package im.bnw.android.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.switchmaterial.SwitchMaterial
import im.bnw.android.R
import im.bnw.android.databinding.FragmentSettingsBinding
import im.bnw.android.databinding.IncludeSettingItemBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.Event
import im.bnw.android.presentation.core.SettingsDialogEvent
import im.bnw.android.presentation.settings.adapter.SettingsDialog
import im.bnw.android.presentation.settings.adapter.SettingsItem
import im.bnw.android.presentation.util.Const
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.setLocale
import im.bnw.android.presentation.util.toItem
import im.bnw.android.presentation.util.toSetting
import im.bnw.android.presentation.util.viewBinding

class SettingsFragment : BaseFragment<SettingsViewModel, SettingsState>(
    R.layout.fragment_settings
) {
    private val binding by viewBinding(FragmentSettingsBinding::bind)
    override val vmClass = SettingsViewModel::class.java

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.setNavigationOnClickListener { viewModel.backPressed() }
            anonymity.setOnCheckedChangeListener { _, enabled ->
                viewModel.anonymityClicked(enabled)
            }
            scrollToReplies.setOnCheckedChangeListener { _, enabled ->
                viewModel.scrollToRepliesChanged(enabled)
            }
            theme.root.setOnClickListener { viewModel.chooseTheme() }
            language.root.setOnClickListener { viewModel.chooseLanguage() }
        }
        setFragmentResultListener(Const.SETTINGS_REQUEST_KEY) { _, bundle ->
            settingsChanged(bundle.getParcelable(Const.ARGUMENT_SETTING))
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is SettingsDialogEvent -> showSettingsDialog(event)
            else -> super.onEvent(event)
        }
    }

    override fun updateState(state: SettingsState) {
        when (state) {
            is SettingsState.Loading -> renderLoading()
            is SettingsState.Idle -> renderIdle(state)
        }
    }

    private fun renderLoading() = with(binding) {
        appBar.isVisible = false
        toolbar.title = getString(R.string.settings)
        loadingBar.root.isVisible = true
        behavior.isVisible = false
        anonymity.isVisible = false
        scrollToReplies.isVisible = false
        appearance.isVisible = false
        theme.root.isVisible = false
        language.root.isVisible = false
    }

    private fun renderIdle(state: SettingsState.Idle) = with(binding) {
        appBar.isVisible = true
        toolbar.title = getString(R.string.settings)
        loadingBar.root.isVisible = false
        behavior.newText = getString(R.string.behavior)
        behavior.isVisible = true
        if (state.user != null) {
            anonymity.drawSwitcher(state.settings.incognito, R.string.anonymity)
        } else {
            anonymity.isVisible = false
        }
        scrollToReplies.drawSwitcher(state.settings.scrollToReplies, R.string.scroll_to_replies)
        appearance.newText = getString(R.string.appearance)
        appearance.isVisible = true
        theme.fillSetting(R.string.theme, state.settings.theme.toItem().nameResId)
        language.fillSetting(R.string.language, state.settings.language.toItem().nameResId)
    }

    private fun SwitchMaterial.drawSwitcher(
        checked: Boolean,
        @StringRes nameResId: Int
    ) {
        if (isChecked != checked) {
            isChecked = checked
        }
        newText = getString(nameResId)
        isVisible = true
    }

    private fun IncludeSettingItemBinding.fillSetting(
        @StringRes nameResId: Int,
        @StringRes valueResId: Int,
    ) {
        name.newText = getString(nameResId)
        value.newText = getString(valueResId)
        root.isVisible = true
    }

    private fun settingsChanged(setting: SettingsItem?) = when (setting) {
        is ThemeItem -> viewModel.themeChanged(setting.toSetting())
        is LanguageItem -> {
            val language = setting.toSetting()
            viewModel.languageChanged(language)
            language.setLocale(requireContext())
        }
        else -> throw IllegalArgumentException("Unknown setting class")
    }

    private fun showSettingsDialog(event: SettingsDialogEvent) =
        showDialog(SettingsDialog.SETTINGS_DIALOG_TAG) {
            SettingsDialog.newInstance(
                getString(event.title),
                event.currentItem,
                event.items,
            )
        }
}
