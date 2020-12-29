package com.jaredzhang.blockviewer.di

import android.app.Application
import com.jaredzhang.blockviewer.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ApplicationScope
@Component(
    modules = [
        NetworkModule::class,
        AndroidSupportInjectionModule::class,
        ActivityModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {

    fun application(): Application

    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
