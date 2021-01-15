package im.bnw.android.di.usermanager

import dagger.Binds
import dagger.Module
import im.bnw.android.domain.usermanager.UserManager
import im.bnw.android.domain.usermanager.UserManagerImpl

@Module
interface UserManagerModule {
    @Binds
    fun provideUserManager(userManager: UserManagerImpl): UserManager
}
