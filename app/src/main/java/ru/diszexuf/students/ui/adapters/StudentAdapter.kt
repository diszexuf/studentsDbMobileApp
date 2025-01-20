package ru.diszexuf.students.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.diszexuf.students.data.entities.Student


// адаптер для recyclerview, отображает список студентов
class StudentAdapter(
    private val onDeleteClick: (Student) -> Unit, // колбэк для удаления студента
    private val onEditClick: (Student) -> Unit // колбэк для редактирования студента
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    // создание viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        // создание представления для элемента списка
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        // возвращение нового viewholder с созданным представлением
        return StudentViewHolder(view)
    }

    // привязка данных к viewholder
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        // получение студента по позиции
        val student = getItem(position)
        // привязка данных студента к viewholder
        holder.bind(student)
    }

    // внутренний класс viewholder для хранения ссылок на представления элемента списка
    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ссылки на textview для отображения данных студента
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        // привязка данных студента к представлениям
        fun bind(student: Student) {
            // установка текста для textview
            text1.text = "${student.firstName} ${student.lastName}"
            text2.text = student.patronymic

            // обработка нажатия на элемент списка для редактирования студента
            itemView.setOnClickListener {
                onEditClick(student)
            }

            // обработка долгого нажатия на элемент списка для удаления студента
            itemView.setOnLongClickListener {
                onDeleteClick(student)
                true
            }
        }
    }
}

// класс для сравнения элементов списка при обновлении данных
class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
    // проверка, являются ли два элемента одним и тем же
    override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem.id == newItem.id
    }

    // проверка, одинаковы ли содержимое двух элементов
    override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem == newItem
    }
}
