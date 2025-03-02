package com.mycelium.servicemonitor.database

import android.content.Context
import androidx.room.Room
import com.mycelium.servicemonitor.repository.CheckHistoryDao
import com.mycelium.servicemonitor.repository.ServiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "service_database"
        ).build()


    @Provides
    fun provideServiceDao(database: AppDatabase): ServiceDao =
        database.serviceDao()

    @Provides
    fun provideHistoryDao(database: AppDatabase): CheckHistoryDao =
        database.historyDao()

}