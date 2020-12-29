package com.jaredzhang.blockviewer.di

import androidx.lifecycle.ViewModel
import com.jaredzhang.blockviewer.MainActivity
import com.jaredzhang.blockviewer.ui.blocklist.BlockListViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.FlowPreview

@Module
abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMainActivity(): MainActivity

}

@FlowPreview
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(BlockListViewModel::class)
    abstract fun bindMainViewModel(viewModel: BlockListViewModel): ViewModel
}