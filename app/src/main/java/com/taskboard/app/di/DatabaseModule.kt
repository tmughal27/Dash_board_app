package com.taskboard.app.di

import android.content.Context
import androidx.room.Room
import com.taskboard.app.data.local.AppDatabase
import com.taskboard.app.data.local.TaskDao
import com.taskboard.app.data.repository.TaskRepository
import com.taskboard.app.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "taskboard.db"
            ).build()
        }

        @Provides
        @Singleton
        fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
    }
}
