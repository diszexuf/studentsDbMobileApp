package ru.diszexuf.students.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.diszexuf.students.data.entities.Student

class StudentAdapter(
    private val onDeleteClick: (Student) -> Unit, // Обработчик для удаления
    private val onEditClick: (Student) -> Unit // Обработчик для редактирования
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(student: Student) {
            text1.text = "${student.firstName} ${student.lastName}"
            text2.text = student.patronymic

            // Обработка клика на элемент для редактирования
            itemView.setOnClickListener {
                onEditClick(student) // Передаем студента для редактирования
            }

            // Добавляем кнопку удаления, которая будет вызывать onDeleteClick
            itemView.setOnLongClickListener {
                onDeleteClick(student) // Передаем студента для удаления
                true
            }
        }
    }
}

class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
    override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem == newItem
    }
}



