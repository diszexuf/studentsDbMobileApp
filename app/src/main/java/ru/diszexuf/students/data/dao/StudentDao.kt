package ru.diszexuf.students.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.diszexuf.students.data.entities.Student

@Dao
interface StudentDao {

    @Insert
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students WHERE groupId = :groupId")
    fun getStudentsByGroup(groupId: Int): LiveData<List<Student>>

    @Query("SELECT * FROM students WHERE lastName LIKE :lastName")
    fun getStudentsByLastName(lastName: String): LiveData<List<Student>>

}