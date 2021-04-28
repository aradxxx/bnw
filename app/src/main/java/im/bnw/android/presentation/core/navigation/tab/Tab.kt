package im.bnw.android.presentation.core.navigation.tab

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import im.bnw.android.R

enum class Tab(
    @DrawableRes
    val icon: Int,
    @StringRes
    val title: Int
) {
    GENERAL(R.drawable.ic_list, R.string.general),
    TODAY(R.drawable.ic_calendar_today, R.string.today),
    PROFILE(R.drawable.ic_profile, R.string.profile);
}
