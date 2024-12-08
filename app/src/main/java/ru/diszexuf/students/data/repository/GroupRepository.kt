package ru.diszexuf.students.data.repository

import androidx.lifecycle.LiveData
import ru.diszexuf.students.data.dao.GroupDao
import ru.diszexuf.students.data.entities.Group

class GroupRepository(private val groupDao: GroupDao) {

    val allGroups: LiveData<List<Group>> = groupDao.getAllGroups()

    suspend fun insert(group: Group) {
        groupDao.insertGroup(group)
    }

    suspend fun update(group: Group) {
        groupDao.updateGroup(group)
    }

    suspend fun delete(group: Group) {
        groupDao.deleteGroup(group)
    }

    suspend fun getGroupById(groupId: Int): LiveData<Group> {
        return groupDao.getGroupById(groupId);
    }

}