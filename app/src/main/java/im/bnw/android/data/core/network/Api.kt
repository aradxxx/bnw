package im.bnw.android.data.core.network

import im.bnw.android.data.auth.AuthResponse
import im.bnw.android.data.message.MessagesResponse
import im.bnw.android.data.message.PostResponse
import im.bnw.android.data.profile.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val QUERY_LOGIN = "login"

interface Api {
    /*Посты списком постранично*/
    @GET("show")
    suspend fun messages(
        @Query("after") after: String,
        @Query("before") before: String,
        @Query("user") user: String?
    ): MessagesResponse

    @GET("passlogin")
    suspend fun login(
        @Query("user") user: String,
        @Query("password") password: String,
    ): AuthResponse

    /*
    @GET("whoami")
    suspend fun whoAmI(
        @Query("login") token: String,
    ): ProfileResponse
    */

    @GET("userinfo")
    suspend fun userInfo(
        @Query("user") user: String,
    ): ProfileResponse

    @POST("post")
    suspend fun post(
        @Query("text") text: String,
        @Query(QUERY_LOGIN) login: String?,
        @Query("anonymous") anonymous: String
    ): PostResponse
}
