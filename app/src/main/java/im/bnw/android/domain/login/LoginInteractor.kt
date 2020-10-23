package im.bnw.android.domain.login

interface LoginInteractor {
    suspend fun auth(userName: String, password: String): String
}
