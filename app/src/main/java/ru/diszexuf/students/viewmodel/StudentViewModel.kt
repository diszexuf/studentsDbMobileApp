package ru.diszexuf.students.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.data.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    fun loadStudents() {
        viewModelScope.launch {
            _students.value = repository.getAllStudents()
        }
    }

    fun filterStudentsByGroup(groupId: Long) {
        viewModelScope.launch {
            _students.value = repository.getStudentsByGroup(groupId)
        }
    }

    fun searchStudentsByLastName(query: String) {
        viewModelScope.launch {
            _students.value = repository.searchStudentsByLastName(query)
        }
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.insertStudent(student)
            loadStudents()
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
            loadStudents()
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            loadStudents()
        }
    }
}