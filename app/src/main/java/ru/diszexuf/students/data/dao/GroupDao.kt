package ru.diszexuf.students.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.diszexuf.students.data.entities.Group

@Dao
interface GroupDao {
    @Insert
    suspend fun insertGroup(group: Group)

    @Update
    suspend fun updateGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("SELECT * FROM groups")
    fun getAllGroups(): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getGroupById(groupId: Int): LiveData<Group>

}