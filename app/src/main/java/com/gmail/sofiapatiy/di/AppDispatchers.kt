package com.gmail.sofiapatiy.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@InstallIn(SingletonComponent::class)
@Module
object AppDispatchers {

    @Provides
    @Singleton
    fun provideExceptionHandler(): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.d("coroutine_exception_handler", "$throwable")
        }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named("Coroutine.Context.IO")
    fun provideIOCoroutineContext(
        @IoDispatcher dispatcher: CoroutineDispatcher,
        exceptionHandler: CoroutineExceptionHandler
    ): CoroutineContext = dispatcher + exceptionHandler
}