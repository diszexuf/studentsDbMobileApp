package ru.diszexuf.students.data.entities

import androidx.room.*

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val birthDate: String,
    @ColumnInfo(name = "groupId") val groupId: Int
)