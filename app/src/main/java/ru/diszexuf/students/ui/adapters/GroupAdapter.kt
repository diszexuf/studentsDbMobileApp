package ru.diszexuf.students.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.diszexuf.students.data.entities.Group

class GroupAdapter(
    private val onDeleteClick: (Group) -> Unit,
    private val onEditClick: (Group) -> Unit // Добавлен обработчик для редактирования
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(group: Group) {
            text1.text = group.groupNumber
            text2.text = group.facultyName

            itemView.setOnClickListener {
                // Обработка клика на группу (редактирование)
                onEditClick(group)
            }

            itemView.setOnLongClickListener {
                // Обработка долгого клика на группу (удаление)
                onDeleteClick(group)
                true
            }
        }
    }
}

class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }
}


