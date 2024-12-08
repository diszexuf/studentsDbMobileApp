package ru.diszexuf.students.data.repository

import androidx.lifecycle.LiveData
import ru.diszexuf.students.data.dao.GroupDao
import ru.diszexuf.students.data.dao.StudentDao
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.entities.Student

class StudentRepository(private val studentDao: StudentDao) {

    fun getStudentByGroup(groupId: Int): LiveData<List<Student>> {
        return studentDao.getStudentsByGroup(groupId)
    }

    fun getStudentByLastName(lastName: String): LiveData<List<Student>> {
        return studentDao.getStudentsByLastName(lastName)
    }

    suspend fun insert(student: Student) {
        studentDao.insertStudent(student)
    }

    suspend fun update(student: Student) {
        studentDao.updateStudent(student)
    }

    suspend fun delete(student: Student) {
        studentDao.deleteStudent(student)
    }

}