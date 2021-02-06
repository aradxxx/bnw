package im.bnw.android.di.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import dagger.Module
import dagger.Provides
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.message.MessageRepositoryImpl
import im.bnw.android.domain.message.MessageRepository
import im.bnw.android.domain.usermanager.UserManager
import javax.inject.Singleton

@Module
class DataModule {
    @Provides
    @Singleton
    fun provideDataStorePreferences(context: Context): DataStore<Preferences> =
        context.createDataStore(name = "user")

    @Provides
    @Singleton
    fun bindMessageRepository(api: Api, userManager: UserManager): MessageRepository =
        MessageRepositoryImpl(api, userManager)
}
