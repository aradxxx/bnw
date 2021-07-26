package im.bnw.android.di.settings

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.bnw.android.di.core.FragmentModule
import im.bnw.android.di.core.keys.ViewModelKey
import im.bnw.android.presentation.settings.SettingsFragment
import im.bnw.android.presentation.settings.SettingsState
import im.bnw.android.presentation.settings.SettingsViewModel

@Module(includes = [SettingsFragmentModule.ViewModelModule::class])
class SettingsFragmentModule : FragmentModule<SettingsFragment, SettingsState>() {
    @Module(includes = [SettingsModule::class])
    interface ViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(SettingsViewModel::class)
        fun bindViewModel(viewModel: SettingsViewModel): ViewModel
    }
}
