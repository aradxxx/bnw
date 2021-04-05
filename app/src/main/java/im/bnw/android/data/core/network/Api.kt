package im.bnw.android.data.core.network

import im.bnw.android.data.auth.AuthResponse
import im.bnw.android.data.core.network.httpresult.HttpResult
import im.bnw.android.data.message.MessageDetailsResponse
import im.bnw.android.data.message.MessagesResponse
import im.bnw.android.data.message.PostResponse
import im.bnw.android.data.profile.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
    ): HttpResult<ProfileResponse>

    @POST("post")
    suspend fun post(
        @Query("text") text: String,
        @Query("anonymous") anonymous: String
    ): PostResponse

    @GET("show")
    suspend fun messageDetail(
        @Query("message") messageId: String,
        @Query("replies") replies: String = "1",
    ): HttpResult<MessageDetailsResponse>
}
