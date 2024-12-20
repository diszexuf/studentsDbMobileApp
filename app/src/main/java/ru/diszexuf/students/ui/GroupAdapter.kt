package ru.diszexuf.students.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.diszexuf.students.databinding.ItemGroupBinding

data class GroupItem(
    val id: Long,
    val groupNumber: String,
    val facultyName: String
)

class GroupAdapter(
    private val onGroupClick: (GroupItem) -> Unit,
    private val onDeleteClick: (GroupItem) -> Unit
) : ListAdapter<GroupItem, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group, onGroupClick, onDeleteClick)
    }

    class GroupViewHolder(private val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            group: GroupItem,
            onGroupClick: (GroupItem) -> Unit,
            onDeleteClick: (GroupItem) -> Unit
        ) {
            binding.groupNumber.text = group.groupNumber
            binding.facultyName.text = group.facultyName
            binding.root.setOnClickListener { onGroupClick(group) }
            binding.deleteButton.setOnClickListener { onDeleteClick(group) }
        }
    }
}

class GroupDiffCallback : DiffUtil.ItemCallback<GroupItem>() {
    override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean = oldItem == newItem
}