package ru.diszexuf.students.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.R
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.ui.adapters.GroupAdapter
import ru.diszexuf.students.viewmodel.GroupViewModel
import ru.diszexuf.students.viewmodel.StudentViewModel

@AndroidEntryPoint
class GroupListFragment : Fragment(R.layout.fragment_group_list) {

    private val groupViewModel: GroupViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels() // Для проверки студентов в группе
    private lateinit var groupAdapter: GroupAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настроим RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewGroups)
        groupAdapter = GroupAdapter(
            onDeleteClick = { group -> showDeleteGroupDialog(group) },
            onEditClick = { group -> navigateToEditGroupFragment(group) } // Передача обработчика для редактирования
        )
        recyclerView.adapter = groupAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Кнопка для добавления новой группы (используем FloatingActionButton)
        val addGroupButton = view.findViewById<FloatingActionButton>(R.id.addGroupButton)
        addGroupButton.setOnClickListener {
            showAddGroupDialog() // Диалог для добавления группы
        }

        // Наблюдаем за списком групп
        groupViewModel.groups.observe(viewLifecycleOwner) { groups ->
            groupAdapter.submitList(groups)
        }
    }

    private fun showAddGroupDialog() {
        // Создаем диалоговое окно для добавления новой группы
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)

        // Находим элементы в макете
        val editTextGroupNumber = dialogView.findViewById<EditText>(R.id.editTextGroupNumber)
        val editTextFacultyName = dialogView.findViewById<EditText>(R.id.editTextFacultyName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить группу")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                // Получаем данные из полей ввода
                val groupNumber = editTextGroupNumber.text.toString().trim()
                val facultyName = editTextFacultyName.text.toString().trim()

                if (groupNumber.isNotEmpty() && facultyName.isNotEmpty()) {
                    // Если данные введены корректно, добавляем новую группу
                    val newGroup = Group(groupNumber = groupNumber, facultyName = facultyName)
                    groupViewModel.addGroup(newGroup)
                } else {
                    Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        // Показываем диалог
        dialog.show()
    }

    // Функция для отображения диалога подтверждения удаления группы
    private fun showDeleteGroupDialog(group: Group) {
        // Проверяем, есть ли студенты в группе перед удалением
        studentViewModel.filterByGroup(group.id).observe(viewLifecycleOwner) { students ->
            if (students.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Удалить группу?")
                    .setMessage("Вы уверены, что хотите удалить группу ${group.groupNumber}?")
                    .setPositiveButton("Да") { _, _ ->
                        groupViewModel.deleteGroup(group) // Удаляем группу
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Невозможно удалить группу, в ней есть студенты", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Навигация к фрагменту редактирования группы
    private fun navigateToEditGroupFragment(group: Group) {
        val action = GroupListFragmentDirections.actionGroupListFragmentToEditGroupFragment(group.id)
        findNavController().navigate(action)
    }
}

