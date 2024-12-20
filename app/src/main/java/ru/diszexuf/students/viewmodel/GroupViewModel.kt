package ru.diszexuf.students.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.repository.GroupRepository
import ru.diszexuf.students.data.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    // LiveData для списка групп
    val groups: LiveData<List<Group>> = groupRepository.getAllGroups()

    // MutableLiveData для ошибок
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    // Добавление новой группы
    fun addGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.insertGroup(group)
        }
    }

    // Удаление группы с проверкой наличия студентов
    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            // Получаем список студентов из репозитория
            val studentsInGroup = studentRepository.getStudentsByGroup(group.id).value

            // Проверяем, есть ли студенты в группе
            if (studentsInGroup.isNullOrEmpty()) {
                // Если студентов нет, можем безопасно удалить группу
                groupRepository.deleteGroup(group)
            } else {
                // Если в группе есть студенты, уведомляем пользователя
                _errorLiveData.value = "Невозможно удалить группу, пока в ней есть студенты"
            }
        }
    }
}
