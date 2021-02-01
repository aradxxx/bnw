package im.bnw.android.presentation.profile

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.databinding.FragmentProfileBinding
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.dialog.NotificationDialog
import im.bnw.android.presentation.util.DialogCode
import im.bnw.android.presentation.util.viewBinding

class ProfileFragment : BaseFragment<ProfileViewModel, ProfileState>(
    R.layout.fragment_profile
) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    override val vmClass = ProfileViewModel::class.java

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            login.setOnClickListener { viewModel.loginClicked() }
            retry.setOnClickListener { viewModel.retryClicked() }
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_logout -> {
                        showDialog {
                            NotificationDialog.newInstance(
                                getString(R.string.log_out_confirmation),
                                DialogCode.LOGOUT_CONFIRM_DIALOG_REQUEST_CODE,
                                null,
                                getString(R.string.cancel),
                                getString(R.string.log_out)
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onNegativeClick(dialog: DialogInterface, which: Int, requestCode: Int) {
        if (requestCode == DialogCode.LOGOUT_CONFIRM_DIALOG_REQUEST_CODE) {
            viewModel.logoutConfirmed()
        }
    }

    override fun updateState(state: ProfileState) {
        when (state) {
            is ProfileState.Init -> {
                renderInitState()
            }
            is ProfileState.Loading -> {
                renderLoadingState()
            }
            is ProfileState.LoadingFailed -> {
                renderLoadingFailedState()
            }
            is ProfileState.Unauthorized -> {
                renderUnauthorizedState()
            }
            is ProfileState.ProfileInfo -> {
                renderProfileInfo(state)
            }
        }
    }

    private fun renderInitState() {
        with(binding) {
            appBar.isVisible = false
            details.detailCard.isVisible = false
            login.isVisible = false
            retry.isVisible = false
            progressBar.isVisible = false
        }
    }

    private fun renderLoadingState() {
        with(binding) {
            appBar.isVisible = false
            details.detailCard.isVisible = false
            login.isVisible = false
            retry.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun renderLoadingFailedState() {
        with(binding) {
            appBar.isVisible = false
            details.detailCard.isVisible = false
            login.isVisible = false
            retry.isVisible = true
            progressBar.isVisible = false
        }
    }

    private fun renderUnauthorizedState() {
        with(binding) {
            appBar.isVisible = false
            details.detailCard.isVisible = false
            login.isVisible = true
            retry.isVisible = false
            progressBar.isVisible = false
        }
    }

    private fun renderProfileInfo(state: ProfileState.ProfileInfo) {
        with(binding) {
            appBar.isVisible = true
            toolbar.title = state.user.name
            details.detailCard.isVisible = true
            login.isVisible = false
            retry.isVisible = false
            progressBar.isVisible = false

            Glide.with(requireContext())
                .load(String.format(BuildConfig.USER_AVA_URL, state.user.name))
                .transform(CircleCrop())
                .into(details.ava)
        }
    }
}
