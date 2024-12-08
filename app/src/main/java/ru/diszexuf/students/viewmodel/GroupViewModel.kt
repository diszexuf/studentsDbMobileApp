package ru.diszexuf.students.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.AppDatabase
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.repository.GroupRepository

class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GroupRepository
    val allGroups: LiveData<List<Group>>

    init {
        val groupDao = AppDatabase.getDatabase(application).groupDao()
        repository = GroupRepository(groupDao)
        allGroups = repository.allGroups
    }

    fun addGroup(group: Group) {
        viewModelScope.launch {
            repository.insert(group)
        }
    }

    fun updateGroup(group: Group) {
        viewModelScope.launch {
            repository.update(group)
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            repository.delete(group)
        }
    }

    fun getGroupById(groupId: Int) {
        viewModelScope.launch {
            repository.getGroupById(groupId)
        }
    }

}