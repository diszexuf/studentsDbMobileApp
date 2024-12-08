package ru.diszexuf.students.data.entities

import androidx.room.*

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: String,
    val facultyName: String
)