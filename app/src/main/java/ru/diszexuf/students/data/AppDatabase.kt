package ru.diszexuf.students.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.diszexuf.students.data.dao.GroupDao
import ru.diszexuf.students.data.dao.StudentDao
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.entities.Student


@Database(entities = [Student::class, Group::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun groupDao(): GroupDao
}
