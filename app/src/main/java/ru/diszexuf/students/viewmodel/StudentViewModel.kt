package ru.diszexuf.students.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.data.repository.GroupRepository
import ru.diszexuf.students.data.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    val students: LiveData<List<Student>> = studentRepository.getAllStudents()

    val groups: LiveData<List<Group>> = groupRepository.getAllGroups()

    // Фильтрация студентов по группе
    fun filterByGroup(groupId: Long): LiveData<List<Student>> {
        return studentRepository.getStudentsByGroup(groupId)
    }

    // Поиск студентов по фамилии
    fun searchByLastName(query: String): LiveData<List<Student>> {
        return studentRepository.searchStudentsByLastName(query)
    }

    // Добавление нового студента
    fun addStudent(student: Student) {
        viewModelScope.launch {
            studentRepository.insertStudent(student)
        }
    }

    // Редактирование студента
    fun updateStudent(student: Student) {
        viewModelScope.launch {
            studentRepository.updateStudent(student)
        }
    }

    // Удаление студента
    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            studentRepository.deleteStudent(student)
        }
    }
}

