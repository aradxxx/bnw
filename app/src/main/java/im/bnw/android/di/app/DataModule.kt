package im.bnw.android.di.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import dagger.Module
import dagger.Provides
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.message.MessageRepositoryImpl
import im.bnw.android.data.user.UserDataStoreImpl
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.core.dispatcher.DispatchersProviderImpl
import im.bnw.android.domain.message.MessageRepository
import im.bnw.android.domain.usermanager.UserDataStore
import javax.inject.Singleton

@Module
class DataModule {
    @Provides
    @Singleton
    fun provideDispatchers(): DispatchersProvider = DispatchersProviderImpl

    @Provides
    @Singleton
    fun provideDataStorePreferences(context: Context): DataStore<Preferences> =
        context.createDataStore("user")

    @Provides
    @Singleton
    fun provideUserDataStore(dataStore: DataStore<Preferences>): UserDataStore = UserDataStoreImpl(dataStore)

    @Provides
    @Singleton
    fun bindMessageRepository(
        api: Api,
        dispatchersProvider: DispatchersProvider
    ): MessageRepository =
        MessageRepositoryImpl(api, dispatchersProvider)
}
