package ru.diszexuf.students.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.AppDatabase
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.data.repository.StudentRepository

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository

    init {
        val studentDao = AppDatabase.getDatabase(application).studentDao()
        repository = StudentRepository(studentDao)
    }

    fun getStudentsByGroup(groupId: Int): LiveData<List<Student>> {
        return repository.getStudentByGroup(groupId)
    }

    fun getStudentsByLastName(lastName: String): LiveData<List<Student>> {
        return repository.getStudentByLastName(lastName)
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.insert(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.update(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.delete(student)
        }
    }
}