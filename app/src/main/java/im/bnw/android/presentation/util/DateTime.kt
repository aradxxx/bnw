package im.bnw.android.presentation.util

import android.content.Context
import im.bnw.android.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val APP_DATE_FORMAT = "dd MMM yyyy HH:mm"
private const val DATE = "dd.MM.yyyy"
private const val TIME = "HH:mm"

fun Long.dateToAppFormat(): String {
    if (isSameDays(System.currentTimeMillis(), this)) {
        return format(TIME)
    }
    return format(APP_DATE_FORMAT)
}

fun Long.formatDateTime(): String {
    return format(APP_DATE_FORMAT)
}

fun Long.format(pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

fun isSameDays(time: Long, prev: Long): Boolean {
    val firstDateString = time.format(DATE)
    val secondDateString = prev.format(DATE)
    return firstDateString == secondDateString
}

fun timeAgoString(context: Context, time: Long): String {
    val diff = System.currentTimeMillis() - time
    val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
    val hours = TimeUnit.MILLISECONDS.toHours(diff).toInt()
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
    return when {
        days > 0 -> context.resources.getQuantityString(R.plurals.days, days, days)
        hours > 0 -> context.resources.getQuantityString(R.plurals.hours, hours, hours)
        else -> context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
    }
}
