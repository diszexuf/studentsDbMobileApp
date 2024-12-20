package ru.diszexuf.students.data.repository

import ru.diszexuf.students.data.dao.StudentDao
import ru.diszexuf.students.data.entities.Student
import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val studentDao: StudentDao
) {
    fun getAllStudents() = studentDao.getAllStudents()

    fun getStudentsByGroup(groupId: Long) = studentDao.getStudentsByGroup(groupId)

    fun searchStudentsByLastName(query: String) = studentDao.searchStudentsByLastName(query)

    suspend fun insertStudent(student: Student) = studentDao.insertStudent(student)

    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)

    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)
}