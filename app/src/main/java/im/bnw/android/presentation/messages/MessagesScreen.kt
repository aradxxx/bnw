package im.bnw.android.presentation.messages

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import ru.terrakok.cicerone.android.support.SupportAppScreen

class MessagesScreen(name: String) : SupportAppScreen() {
    private val params: MessagesScreenParams = MessagesScreenParams(name)
    override fun getFragment(): Fragment = MessagesFragment.newInstance(params)
}

@Parcelize
data class MessagesScreenParams(val user: String) : Parcelable
