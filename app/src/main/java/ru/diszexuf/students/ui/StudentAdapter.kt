package ru.diszexuf.students.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.diszexuf.students.databinding.ItemStudentBinding

data class StudentItem(
    val id: Long,
    val fullName: String, // Формат: "Фамилия Имя Отчество"
    val birthDate: String,
    val groupNumber: String
)

class StudentAdapter(
    private val onStudentClick: (StudentItem) -> Unit,
    private val onDeleteClick: (StudentItem) -> Unit
) : ListAdapter<StudentItem, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student, onStudentClick, onDeleteClick)
    }

    class StudentViewHolder(private val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            student: StudentItem,
            onStudentClick: (StudentItem) -> Unit,
            onDeleteClick: (StudentItem) -> Unit
        ) {
            binding.fullName.text = student.fullName
            binding.birthDate.text = student.birthDate
            binding.groupNumber.text = student.groupNumber
            binding.root.setOnClickListener { onStudentClick(student) }
            binding.deleteButton.setOnClickListener { onDeleteClick(student) }
        }
    }
}

class StudentDiffCallback : DiffUtil.ItemCallback<StudentItem>() {
    override fun areItemsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: StudentItem, newItem: StudentItem): Boolean = oldItem == newItem
}
