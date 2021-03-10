@file:Suppress("MagicNumber")

package im.bnw.android.di.app

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import im.bnw.android.BuildConfig
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.core.network.ConnectionInterceptor
import im.bnw.android.data.core.network.connectionprovider.AndroidConnectionProvider
import im.bnw.android.data.core.network.connectionprovider.ConnectionProvider
import im.bnw.android.data.message.ContentDto
import im.bnw.android.data.message.ContentDtoDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
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
    fun provideApi(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Api =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(Api::class.java)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(ContentDto::class.java, ContentDtoDeserializer())
        .build()
}
