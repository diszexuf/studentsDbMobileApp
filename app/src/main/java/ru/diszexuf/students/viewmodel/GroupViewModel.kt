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

    val groups: LiveData<List<Group>> = groupRepository.getAllGroups()

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    fun addGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.insertGroup(group)
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            val studentsInGroup = studentRepository.getStudentsByGroup(group.id).value

            if (studentsInGroup.isNullOrEmpty()) {
                groupRepository.deleteGroup(group)
            } else {
                _errorLiveData.value = "Невозможно удалить группу, пока в ней есть студенты"
            }
        }
    }
}
