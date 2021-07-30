package im.bnw.android.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.databinding.FragmentProfileBinding
import im.bnw.android.databinding.IncludeProfileDetailsBinding
import im.bnw.android.databinding.ItemProfileDetailCardBinding
import im.bnw.android.domain.user.User
import im.bnw.android.presentation.core.BaseFragment
import im.bnw.android.presentation.core.view.FailureView
import im.bnw.android.presentation.util.REG_DATE
import im.bnw.android.presentation.util.UserNotFound
import im.bnw.android.presentation.util.format
import im.bnw.android.presentation.util.networkFailureMessage
import im.bnw.android.presentation.util.newText
import im.bnw.android.presentation.util.viewBinding
import im.bnw.android.presentation.util.withInitialArguments

class ProfileFragment : BaseFragment<ProfileViewModel, ProfileState>(
    R.layout.fragment_profile
) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    override val vmClass = ProfileViewModel::class.java

    companion object {
        fun newInstance(params: ProfileScreenParams) =
            ProfileFragment().withInitialArguments(params)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.setNavigationOnClickListener { viewModel.backPressed() }
            failure.setActionListener { viewModel.retryClicked() }
            details.messagesCount.detail.setOnClickListener { viewModel.messagesClicked() }
            details.avatar.setOnClickListener {
                viewModel.avatarClicked()
            }
        }
    }

    override fun updateState(state: ProfileState) {
        when (state) {
            is ProfileState.Loading -> renderLoading()
            is ProfileState.Failed -> renderFailed(state)
            is ProfileState.ProfileInfo -> renderProfileInfo(state)
        }
    }

    private fun renderLoading() = with(binding) {
        appBar.isVisible = false
        details.drawDetails(null)
        failure.isVisible = false
        progressBar.isVisible = true
    }

    private fun renderFailed(state: ProfileState.Failed) = with(binding) {
        appBar.isVisible = false
        details.drawDetails(null)
        if (state.throwable is UserNotFound) {
            failure.showFailure(R.string.error, R.string.user_not_found, R.drawable.ic_anon)
        } else {
            failure.showFailure(R.string.no_connection, networkFailureMessage(state.throwable))
        }
        progressBar.isVisible = false
    }

    private fun renderProfileInfo(state: ProfileState.ProfileInfo) = with(binding) {
        appBar.isVisible = true
        toolbar.title = state.user.name
        details.drawDetails(state.user)
        failure.isVisible = false
        progressBar.isVisible = false
    }

    private fun IncludeProfileDetailsBinding.drawDetails(user: User?) {
        if (user == null) {
            root.isVisible = false
            return
        }
        root.isVisible = true
        userName.isVisible = false
        val firstJoinTimestamp = user.timestamp()
        if (firstJoinTimestamp != 0L) {
            firstJoinDate.newText = getString(
                R.string.registered,
                firstJoinTimestamp.format(REG_DATE)
            )
        } else {
            firstJoinDate.isVisible = false
        }
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
        @DrawableRes iconResId: Int = R.drawable.ic_google_downasaur
    ) {
        isVisible = true
        title = getString(titleResId)
        message = getString(messageResId)
        imageResId = iconResId
    }
}
