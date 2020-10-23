package im.bnw.android.data.core.network

import im.bnw.android.data.login.LoginResponse
import im.bnw.android.data.message.MessagesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    /*Посты списком постранично*/
    @GET("show")
    suspend fun getMessages(
        @Query("after") after: String,
        @Query("before") before: String,
        @Query("user") user: String?
    ): MessagesResponse

    @GET("passlogin")
    suspend fun getLogin(
        @Query("user") user: String,
        @Query("password") password: String,
    ): LoginResponse
}
