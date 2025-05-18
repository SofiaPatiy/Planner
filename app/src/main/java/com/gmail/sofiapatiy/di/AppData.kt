package com.gmail.sofiapatiy.di

import android.content.Context
import androidx.room.Room
import com.gmail.sofiapatiy.data.PlannerRoomDatabase
import com.gmail.sofiapatiy.data.dao.TaskDao
import com.gmail.sofiapatiy.data.dao.UserDao
import com.gmail.sofiapatiy.repository.PlannerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppData {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): PlannerRoomDatabase =
        Room.inMemoryDatabaseBuilder(context, PlannerRoomDatabase::class.java).build()

    @Singleton
    @Provides
    fun provideUserDao(database: PlannerRoomDatabase): UserDao =
        database.getUserDao()

    @Singleton
    @Provides
    fun provideTaskDao(database: PlannerRoomDatabase): TaskDao =
        database.getTaskDao()

    @Singleton
    @Provides
    fun provideRepository(userDao: UserDao, taskDao: TaskDao): PlannerRepository =
        PlannerRepository(userDao, taskDao)
}