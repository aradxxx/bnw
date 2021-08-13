package im.bnw.android.presentation.settings.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import im.bnw.android.R
import im.bnw.android.databinding.FragmentSettingsDialogBinding
import im.bnw.android.presentation.util.Const.BUNDLE_INITIAL_ARGS
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments

class SettingsDialogFragment : BottomSheetDialogFragment() {
    private val binding by viewBinding(FragmentSettingsDialogBinding::bind)
    private lateinit var settingsAdapter: SettingsAdapter

    companion object {
        const val SETTINGS_DIALOG_TAG = "SETTINGS_DIALOG_TAG"
        const val SETTINGS_DIALOG_REQUEST_KEY = "SETTINGS_DIALOG_REQUEST_KEY"

        const val SETTINGS_SETTING = "SETTINGS_SETTING"

        @JvmStatic
        @JvmName("newInstance")
        operator fun invoke(params: () -> SettingsDialogParams) =
            SettingsDialogFragment().withInitialArguments(params())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = arguments?.getParcelable<SettingsDialogParams>(BUNDLE_INITIAL_ARGS)
            ?: throw IllegalStateException("Settings dialog params is null")
        settingsAdapter = SettingsAdapter(params.currentItem) {
            dismiss()
            parentFragmentManager.setFragmentResult(
                SETTINGS_DIALOG_REQUEST_KEY,
                bundleOf(SETTINGS_SETTING to it)
            )
        }.apply {
            items = params.items
        }
        with(binding.title) {
            newText = params.title
            isVisible = params.title.isNotEmpty()
        }
        with(binding.itemsList) {
            layoutManager = LinearLayoutManager(context)
            adapter = settingsAdapter
        }
    }
}
