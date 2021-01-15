package im.bnw.android.presentation.util

import android.content.res.Resources
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.github.aradxxx.ciceroneflow.FlowCicerone
import com.github.aradxxx.ciceroneflow.FlowNavigator
import im.bnw.android.presentation.core.FragmentViewBindingDelegate
import im.bnw.android.presentation.core.navigation.AppRouter
import im.bnw.android.presentation.main.MainActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

fun Fragment.tabNavigator(
    flowCicerone: FlowCicerone<AppRouter>,
    container: Int
): FlowNavigator<AppRouter> {
    val activity: MainActivity = requireActivity() as MainActivity
    return FlowNavigator(
        activity,
        container,
        flowCicerone,
        activity.supportFragmentManager,
        childFragmentManager
    )
}

fun <F : Fragment> F.withInitialArguments(params: Parcelable) = apply {
    arguments = bundleOf(Const.BUNDLE_INITIAL_ARGS to params)
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<T>,
    defaultValue: T
): Flow<T> {
    return data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { prefs ->
        prefs[key] ?: defaultValue
    }
}

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    edit { prefs -> prefs[key] = value }
}

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

var TextView.newText: String
    get() = text.toString()
    set(newText) {
        if (text.toString() != newText) {
            text = newText
        }
    }
