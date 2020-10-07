@file:Suppress("MagicNumber")

package im.bnw.android.di.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import im.bnw.android.BuildConfig
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.ConnectionInterceptor
import im.bnw.android.data.core.network.connectionprovider.AndroidConnectionProvider
import im.bnw.android.data.core.network.connectionprovider.ConnectionProvider
import im.bnw.android.domain.message.Content
import im.bnw.android.domain.message.ContentDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(Content::class.java, ContentDeserializer())
        .create()

    @Provides
    @Singleton
    fun connectionProvider(context: Context): ConnectionProvider =
        AndroidConnectionProvider(context)

    @Provides
    @Singleton
    fun provideOkHttp(connectionProvider: ConnectionProvider): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }
        builder.addInterceptor(ConnectionInterceptor(connectionProvider))

        return builder
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(okHttpClient: OkHttpClient, gson: Gson): Api =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
}
