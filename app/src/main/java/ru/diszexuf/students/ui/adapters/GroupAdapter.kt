package ru.diszexuf.students.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.diszexuf.students.data.entities.Group

// адаптер для recyclerview, отображает список групп
class GroupAdapter(
    private val onDeleteClick: (Group) -> Unit, // колбэк для удаления группы
    private val onEditClick: (Group) -> Unit // колбэк для редактирования группы
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    // создание viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        // создание представления для элемента списка
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        // возвращение нового viewholder с созданным представлением
        return GroupViewHolder(view)
    }

    // привязка данных к viewholder
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        // получение группы по позиции
        val group = getItem(position)
        // привязка данных группы к viewholder
        holder.bind(group)
    }

    // внутренний класс viewholder для хранения ссылок на представления элемента списка
    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ссылки на textview для отображения данных группы
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        // привязка данных группы к представлениям
        fun bind(group: Group) {
            // установка текста для textview
            text1.text = group.groupNumber
            text2.text = group.facultyName

            // обработка нажатия на элемент списка для редактирования группы
            itemView.setOnClickListener {
                onEditClick(group)
            }

            // обработка долгого нажатия на элемент списка для удаления группы
            itemView.setOnLongClickListener {
                onDeleteClick(group)
                true
            }
        }
    }
}

// класс для сравнения элементов списка при обновлении данных
class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    // проверка, являются ли два элемента одним и тем же
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    // проверка, одинаковы ли содержимое двух элементов
    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }
}


