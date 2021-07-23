package im.bnw.android.presentation.settings.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import im.bnw.android.R
import im.bnw.android.presentation.util.Const.ARGUMENT_SETTING
import im.bnw.android.presentation.util.Const.SETTINGS_REQUEST_KEY
import im.bnw.android.presentation.util.newText

class SettingsDialog : BottomSheetDialogFragment() {
    private lateinit var title: String
    private lateinit var currentItem: SettingsItem
    private lateinit var items: List<SettingsItem>

    private lateinit var settingsAdapter: SettingsAdapter

    companion object {
        const val SETTINGS_DIALOG_TAG = "SETTINGS_DIALOG_TAG"

        private const val ARGUMENT_TITLE = "ARGUMENT_TITLE"
        private const val ARGUMENT_CURRENT_ITEM = "ARGUMENT_CURRENT_ITEM"
        private const val ARGUMENT_ITEMS = "ARGUMENT_ITEMS"

        fun newInstance(
            title: String = "",
            currentItem: SettingsItem,
            items: List<SettingsItem>,
        ): SettingsDialog = SettingsDialog().apply {
            arguments = bundleOf(
                ARGUMENT_TITLE to title,
                ARGUMENT_CURRENT_ITEM to currentItem,
                ARGUMENT_ITEMS to items,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            title = getString(ARGUMENT_TITLE, "")
            getParcelable<SettingsItem>(ARGUMENT_CURRENT_ITEM)?.also { currentItem = it }
            getParcelableArrayList<SettingsItem>(ARGUMENT_ITEMS)?.also { items = it }
        } ?: throw IllegalStateException("SettingsDialog arguments null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_dialog, container, false)
        with(view.findViewById<TextView>(R.id.title)) {
            newText = title
            isVisible = title.isNotEmpty()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsAdapter = SettingsAdapter(currentItem) {
            dismiss()
            parentFragment?.setFragmentResult(
                SETTINGS_REQUEST_KEY,
                bundleOf(ARGUMENT_SETTING to it)
            )
        }
        settingsAdapter.items = items
        view.findViewById<RecyclerView>(R.id.items_list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = settingsAdapter
        }
    }
}
