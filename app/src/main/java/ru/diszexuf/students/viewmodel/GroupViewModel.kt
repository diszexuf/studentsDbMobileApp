package ru.diszexuf.students.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.repository.GroupRepository
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: GroupRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    fun loadGroups() {
        viewModelScope.launch {
            _groups.value = repository.getAllGroups()
        }
    }

    fun addGroup(group: Group) {
        viewModelScope.launch {
            repository.insertGroup(group)
            loadGroups()
        }
    }

    fun updateGroup(group: Group) {
        viewModelScope.launch {
            repository.updateGroup(group)
            loadGroups()
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            repository.deleteGroup(group)
            loadGroups()
        }
    }
}