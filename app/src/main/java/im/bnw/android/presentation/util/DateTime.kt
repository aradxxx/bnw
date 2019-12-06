package im.bnw.android.presentation.util

import android.content.Context
import im.bnw.android.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val APP_DATE_FORMAT = "dd MMM yyyy HH:mm"
private const val DATE = "dd.MM.yyyy"
private const val TIME = "HH:mm"

fun dateToAppFormat(time: Long): String {
    if (isSameDays(System.currentTimeMillis(), time)) {
        return format(time, TIME)
    }
    return format(time, APP_DATE_FORMAT)
}

fun formatDateTime(time: Long): String {
    return format(time, APP_DATE_FORMAT)
}

fun format(time: Long, pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(time))
}

fun isSameDays(time: Long, prev: Long): Boolean {
    val firstDateString = format(time, DATE)
    val secondDateString = format(prev, DATE)
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
