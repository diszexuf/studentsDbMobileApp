package ru.diszexuf.students.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.diszexuf.students.data.entities.Student

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    fun getAllStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM students WHERE groupId = :groupId")
    fun getStudentsByGroup(groupId: Long): LiveData<List<Student>>

    @Query("SELECT * FROM students WHERE lastName LIKE :query")
    fun searchStudentsByLastName(query: String): LiveData<List<Student>>

    @Insert
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}
