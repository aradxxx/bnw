package im.bnw.android.di.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.LocaleStore
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import dagger.Module
import dagger.Provides
import im.bnw.android.data.core.network.Api
import im.bnw.android.data.message.MessageRepositoryImpl
import im.bnw.android.data.settings.SettingsRepositoryImpl
import im.bnw.android.data.user.UserDataStoreImpl
import im.bnw.android.domain.core.dispatcher.DispatchersProvider
import im.bnw.android.domain.core.dispatcher.DispatchersProviderImpl
import im.bnw.android.domain.message.MessageRepository
import im.bnw.android.domain.settings.SettingsRepository
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
    fun provideUserDataStore(dataStore: DataStore<Preferences>): UserDataStore =
        UserDataStoreImpl(dataStore)

    @Provides
    @Singleton
    fun provideLocaleStore(context: Context): LocaleStore =
        PreferenceLocaleStore(context)

    @Provides
    @Singleton
    fun provideLingver(): Lingver = Lingver.getInstance()

    @Provides
    @Singleton
    fun bindMessageRepository(
        api: Api,
        dispatchersProvider: DispatchersProvider
    ): MessageRepository =
        MessageRepositoryImpl(api, dispatchersProvider)

    @Provides
    @Singleton
    fun bindSettingsRepository(
        dataStore: DataStore<Preferences>,
    ): SettingsRepository =
        SettingsRepositoryImpl(dataStore)
}
