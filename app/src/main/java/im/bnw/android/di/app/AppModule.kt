package im.bnw.android.di.app

import dagger.Module
import im.bnw.android.di.auth.AuthModule
import im.bnw.android.di.message.MessageModule
import im.bnw.android.di.profile.ProfileModule
import im.bnw.android.di.settings.SettingsModule
import im.bnw.android.di.usermanager.UserManagerModule

@Module(
    includes = [
        MessageModule::class,
        AuthModule::class,
        ProfileModule::class,
        SettingsModule::class,
        UserManagerModule::class,
    ]
)
interface AppModule
