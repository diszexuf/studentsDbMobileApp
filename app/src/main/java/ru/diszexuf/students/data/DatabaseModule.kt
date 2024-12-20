package ru.diszexuf.students.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.diszexuf.students.data.dao.GroupDao
import ru.diszexuf.students.data.dao.StudentDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
    }

    @Provides
    fun provideStudentDao(database: AppDatabase): StudentDao = database.studentDao()

    @Provides
    fun provideGroupDao(database: AppDatabase): GroupDao = database.groupDao()
}