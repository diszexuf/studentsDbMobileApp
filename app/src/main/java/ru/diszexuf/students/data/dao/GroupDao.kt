package ru.diszexuf.students.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.diszexuf.students.data.entities.Group

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups")
    fun getAllGroups(): LiveData<List<Group>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Update
    suspend fun updateGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)
}