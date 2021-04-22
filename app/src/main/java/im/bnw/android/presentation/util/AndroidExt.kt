@file:Suppress("TooManyFunctions")

package im.bnw.android.presentation.util

import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.yariksoffice.lingver.Lingver
import im.bnw.android.domain.settings.LanguageSettings
import im.bnw.android.domain.settings.ThemeSettings
import im.bnw.android.presentation.core.FragmentViewBindingDelegate
import im.bnw.android.presentation.user.LanguageItem
import im.bnw.android.presentation.user.ThemeItem
import java.util.Locale

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
        }
    }
}

fun ThemeSettings.toItem(): ThemeItem = when (this) {
    ThemeSettings.Default -> ThemeItem.Default
    ThemeSettings.Light -> ThemeItem.Light
    ThemeSettings.Dark -> ThemeItem.Dark
}

fun LanguageSettings.toItem(): LanguageItem = when (this) {
    LanguageSettings.Default -> LanguageItem.Default
    LanguageSettings.English -> LanguageItem.English
    LanguageSettings.Russian -> LanguageItem.Russian
}

fun ThemeItem.toSetting(): ThemeSettings = when (this) {
    ThemeItem.Default -> ThemeSettings.Default
    ThemeItem.Light -> ThemeSettings.Light
    ThemeItem.Dark -> ThemeSettings.Dark
}

fun LanguageItem.toSetting(): LanguageSettings = when (this) {
    LanguageItem.Default -> LanguageSettings.Default
    LanguageItem.English -> LanguageSettings.English
    LanguageItem.Russian -> LanguageSettings.Russian
}

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
    StfalconImageViewer.Builder(requireContext(), urls) { view, image ->
        Glide.with(requireContext())
            .load(image)
            .into(view)
    }
        .withStartPosition(urls.indexOf(selected))
        .withHiddenStatusBar(false)
        .show()
}
/*endregion open media */

fun RecyclerView.disableItemChangedAnimation() {
    val animator = itemAnimator
    if (animator is SimpleItemAnimator) {
        animator.supportsChangeAnimations = false
    }
}
