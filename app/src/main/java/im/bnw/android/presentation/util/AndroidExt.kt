@file:Suppress("TooManyFunctions", "MagicNumber")

package im.bnw.android.presentation.util

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Parcelable
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.yariksoffice.lingver.Lingver
import im.bnw.android.BuildConfig
import im.bnw.android.R
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.TabSettings
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.FragmentViewBindingDelegate
import im.bnw.android.presentation.core.view.MediaOverlayView
import im.bnw.android.presentation.settings.LanguageItem
import im.bnw.android.presentation.settings.TabSettingsItem
import im.bnw.android.presentation.settings.ThemeItem
import java.util.Locale

val <T> T.exhaustive: T
    get() = this

val Context.dataStore by preferencesDataStore("user")

fun <F : Fragment> F.withInitialArguments(params: Parcelable) = apply {
    arguments = bundleOf(Const.BUNDLE_INITIAL_ARGS to params)
}

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

fun LanguageSettings.setLocale(context: Context) {
    Lingver.getInstance().let {
        when (this) {
            LanguageSettings.Default -> it.setFollowSystemLocale(context)
            LanguageSettings.English -> it.setLocale(context, Locale.ENGLISH)
            LanguageSettings.Russian -> it.setLocale(context, Locales.RUSSIAN)
        }.exhaustive
    }
}

fun ThemeSettings.toItem(): ThemeItem = when (this) {
    ThemeSettings.Default -> ThemeItem.Default
    ThemeSettings.Light -> ThemeItem.Light
    ThemeSettings.Dark -> ThemeItem.Dark
}.exhaustive

fun LanguageSettings.toItem(): LanguageItem = when (this) {
    LanguageSettings.Default -> LanguageItem.Default
    LanguageSettings.English -> LanguageItem.English
    LanguageSettings.Russian -> LanguageItem.Russian
}.exhaustive

fun ThemeItem.toSetting(): ThemeSettings = when (this) {
    ThemeItem.Default -> ThemeSettings.Default
    ThemeItem.Light -> ThemeSettings.Light
    ThemeItem.Dark -> ThemeSettings.Dark
}.exhaustive

fun LanguageItem.toSetting(): LanguageSettings = when (this) {
    LanguageItem.Default -> LanguageSettings.Default
    LanguageItem.English -> LanguageSettings.English
    LanguageItem.Russian -> LanguageSettings.Russian
}.exhaustive

fun TabSettings.toItem(): TabSettingsItem = when (this) {
    TabSettings.Hot -> TabSettingsItem.Hot
    TabSettings.Messages -> TabSettingsItem.Messages
    TabSettings.User -> TabSettingsItem.User
}.exhaustive

fun TabSettingsItem.toSetting(): TabSettings = when (this) {
    TabSettingsItem.Hot -> TabSettings.Hot
    TabSettingsItem.Messages -> TabSettings.Messages
    TabSettingsItem.User -> TabSettings.User
}.exhaustive

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.dpToPxF: Float
    get() = this * Resources.getSystem().displayMetrics.density

@ColorInt
fun Context.attrColor(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

var TextView.newText: String
    get() = text.toString()
    set(newText) {
        if (text.toString() != newText) {
            text = newText
        }
    }

fun ImageView.loadCircleAvatar(context: Context, otherUrlPart: String) {
    Glide.with(context)
        .load(String.format(BuildConfig.USER_AVA_THUMB_URL, otherUrlPart))
        .transform(CircleCrop())
        .into(this)
}

/**
 * Show system keyboard. if [view] not null, then keyboard will focused on this,
 * otherwise default keyboard behaviour.
 *
 * @param view the view to focus when keyboard shown.
 */
fun Fragment.showKeyboard(view: View? = null) {
    if (view != null) {
        ViewCompat.getWindowInsetsController(view)?.show(WindowInsetsCompat.Type.ime())
    } else {
        showSystemUI(WindowInsetsCompat.Type.ime())
    }
}

fun Fragment.hideKeyboard() {
    hideSystemUI(WindowInsetsCompat.Type.ime())
}

fun Fragment.showSystemUI(windowInsetsTypes: Int) =
    view?.let {
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(windowInsetsTypes)
        }
    }

fun Fragment.hideSystemUI(windowInsetsTypes: Int) =
    view?.let {
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(windowInsetsTypes)
        }
    }

/*region throttled click listener*/
private var lastClickTimestamp = 0L
fun View.setThrottledClickListener(delay: Long = 500L, clickListener: (View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - lastClickTimestamp
        if (delta !in 0..delay) {
            lastClickTimestamp = currentTime
            clickListener(this)
        }
    }
}
/*endregion throttled click listener*/

/*region open media */
fun Fragment.openMedia(urls: List<String>, selected: String) {
    requireContext().apply {
        val startPosition = urls.indexOf(selected)
        val overlay = MediaOverlayView(this)
        val imageLoader = ImageLoader<String> { view, image ->
            Glide.with(this)
                .load(image)
                .error(R.drawable.ic_media_load_error)
                .placeholder(R.drawable.ic_media_placeholder)
                .into(view)
        }
        val viewer = StfalconImageViewer.Builder(this, urls, imageLoader)
            .withStartPosition(startPosition)
            .withOverlayView(overlay)
            .withImageChangeListener {
                overlay.updatePosition(it + 1, urls.size)
            }
            .withHiddenStatusBar(false)
            .show()
        overlay.setNavigationOnClickListener { viewer.dismiss() }
        overlay.updatePosition(startPosition + 1, urls.size)
    }
}
/*endregion open media */

fun RecyclerView.disableItemChangedAnimation() {
    val animator = itemAnimator
    if (animator is SimpleItemAnimator) {
        animator.supportsChangeAnimations = false
    }
}

@Suppress("DEPRECATION")
fun Context.vibrate(amplitude: Int = 100) {
    val vibrationService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrationService.vibrate(VibrationEffect.createOneShot(30L, amplitude))
    } else {
        vibrationService.vibrate(30L)
    }
}

fun RecyclerView.ViewHolder.doIfPositionValid(block: (Int) -> Unit) {
    val position = bindingAdapterPosition
    if (position != RecyclerView.NO_POSITION) {
        block(position)
    }
}