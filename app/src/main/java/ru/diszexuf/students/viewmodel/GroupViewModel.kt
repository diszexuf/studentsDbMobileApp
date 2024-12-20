package ru.diszexuf.students.viewmodel

import androidx.lifecycle.LiveData
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

    // Добавление новой группы
    fun addGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.insertGroup(group)
        }
    }

    // Удаление группы с проверкой
    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.deleteGroup(group)
        }
    }
}
