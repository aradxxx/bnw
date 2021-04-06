package im.bnw.android.presentation.util

import java.io.IOException

class AuthFailedException : IOException()
data class BnwApiError(val description: String) : Throwable()
