package com.jaredzhang.blockviewer.di

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Keep
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
