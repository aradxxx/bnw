package im.bnw.android.presentation.imageview

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import ru.terrakok.cicerone.android.support.SupportAppScreen

class ImageScreen(url: String) : SupportAppScreen() {
    private val params: ImageScreenParams = ImageScreenParams(url)
    override fun getFragment(): Fragment = ImageFragment.newInstance(params)
}

@Parcelize
data class ImageScreenParams(val url: String) : Parcelable
