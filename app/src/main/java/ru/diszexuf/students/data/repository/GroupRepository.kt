package ru.diszexuf.students.data.repository

import ru.diszexuf.students.data.dao.GroupDao
import ru.diszexuf.students.data.entities.Group
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    fun getAllGroups() = groupDao.getAllGroups()

    suspend fun insertGroup(group: Group) = groupDao.insertGroup(group)

    suspend fun updateGroup(group: Group) = groupDao.updateGroup(group)

    suspend fun deleteGroup(group: Group) = groupDao.deleteGroup(group)
}