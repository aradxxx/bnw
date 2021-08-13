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
import im.bnw.android.presentation.core.LanguageChangedEvent
import im.bnw.android.presentation.core.SettingsDialogEvent
import im.bnw.android.presentation.settings.adapter.SettingsDialogFragment
import im.bnw.android.presentation.settings.adapter.SettingsDialogFragment.Companion.SETTINGS_DIALOG_REQUEST_KEY
import im.bnw.android.presentation.settings.adapter.SettingsDialogFragment.Companion.SETTINGS_DIALOG_TAG
import im.bnw.android.presentation.settings.adapter.SettingsDialogFragment.Companion.SETTINGS_SETTING
import im.bnw.android.presentation.settings.adapter.SettingsDialogParams
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.setLocale
import im.bnw.android.presentation.util.toItem
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
            toolbar.setNavigationOnClickListener {
                viewModel.backPressed()
            }
            anonymity.setOnCheckedChangeListener { _, enabled ->
                viewModel.anonymityClicked(enabled)
            }
            scrollToReplies.setOnCheckedChangeListener { _, enabled ->
                viewModel.scrollToRepliesChanged(enabled)
            }
            savePostDraft.setOnCheckedChangeListener { _, enabled ->
                viewModel.savePostDraftChanged(enabled)
            }
            transitionAnimations.setOnCheckedChangeListener { _, enabled ->
                viewModel.transitionAnimationsChanged(enabled)
            }
            theme.root.setOnClickListener {
                viewModel.chooseTheme()
            }
            language.root.setOnClickListener {
                viewModel.chooseLanguage()
            }
            defaultTab.root.setOnClickListener {
                viewModel.chooseDefaultTab()
            }
        }
        setFragmentResultListener(SETTINGS_DIALOG_REQUEST_KEY) { _, bundle ->
            viewModel.choiceSettingsChanged(bundle.getParcelable(SETTINGS_SETTING))
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is SettingsDialogEvent -> {
                showSettingsDialog(event)
            }
            is LanguageChangedEvent -> {
                event.language.setLocale(requireContext())
            }
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
        savePostDraft.isVisible = false
        transitionAnimations.isVisible = false
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
            savePostDraft.drawSwitcher(state.settings.savePostDraft, R.string.save_post_draft)
        } else {
            anonymity.isVisible = false
            savePostDraft.isVisible = false
        }
        scrollToReplies.drawSwitcher(state.settings.scrollToReplies, R.string.scroll_to_replies)
        transitionAnimations.drawSwitcher(
            state.settings.transitionAnimations,
            R.string.transition_animations
        )
        appearance.newText = getString(R.string.appearance)
        appearance.isVisible = true
        theme.fillSetting(R.string.theme, state.settings.theme.toItem().nameResId)
        language.fillSetting(R.string.language, state.settings.language.toItem().nameResId)
        defaultTab.fillSetting(R.string.default_tab, state.settings.defaultTab.toItem().nameResId)
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

    private fun showSettingsDialog(event: SettingsDialogEvent) = showDialog(SETTINGS_DIALOG_TAG) {
        SettingsDialogFragment {
            SettingsDialogParams(
                title = getString(event.title),
                currentItem = event.currentItem,
                items = event.items,
            )
        }
    }
}
