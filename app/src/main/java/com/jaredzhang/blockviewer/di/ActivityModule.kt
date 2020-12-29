package com.jaredzhang.blockviewer.di

import androidx.lifecycle.ViewModel
import com.jaredzhang.blockviewer.MainActivity
import com.jaredzhang.blockviewer.ui.blocklist.BlockListViewModel
import com.jaredzhang.blockviewer.ui.detail.BlockDetailActivity
import com.jaredzhang.blockviewer.ui.detail.BlockDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.FlowPreview

@FlowPreview
@Module
abstract class ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindDetailActivity(): BlockDetailActivity

}

@FlowPreview
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(BlockListViewModel::class)
    abstract fun bindMainViewModel(viewModel: BlockListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlockDetailViewModel::class)
    abstract fun bindDetailViewModel(viewModel: BlockDetailViewModel): ViewModel
}