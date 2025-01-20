package ru.diszexuf.students.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
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

    private val groupViewModel: GroupViewModel by viewModels() // viewmodel для работы с группами
    private val studentViewModel: StudentViewModel by viewModels() // viewmodel для работы со студентами
    private lateinit var groupAdapter: GroupAdapter // адаптер для recyclerview

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // инициализация recyclerview и адаптера
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewGroups)
        groupAdapter = GroupAdapter(
            onDeleteClick = { group -> showDeleteGroupDialog(group) },
            onEditClick = { group -> navigateToEditGroupFragment(group) }
        )
        recyclerView.adapter = groupAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // обработка нажатия на кнопку добавления группы
        val addGroupButton = view.findViewById<FloatingActionButton>(R.id.addGroupButton)
        addGroupButton.setOnClickListener {
            showAddGroupDialog()
        }

        // наблюдение за изменениями в списке групп и обновление адаптера
        groupViewModel.groups.observe(viewLifecycleOwner) { groups ->
            groupAdapter.submitList(groups)
        }
    }

    // метод для отображения диалога добавления группы
    private fun showAddGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)

        val editTextGroupNumber = dialogView.findViewById<EditText>(R.id.editTextGroupNumber)
        val editTextFacultyName = dialogView.findViewById<EditText>(R.id.editTextFacultyName)

        // фильтр для поля ввода названия факультета, разрешающий только буквы
        editTextFacultyName.filters = arrayOf(
            InputFilter { source, start, end, dest, dstart, dend ->
                if (source.matches("[a-zA-Zа-яА-Я]+".toRegex())) {
                    null
                } else {
                    ""
                }
            }
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить группу")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val groupNumber = editTextGroupNumber.text.toString().trim()
                val facultyName = editTextFacultyName.text.toString().trim()

                if (groupNumber.isNotEmpty() && facultyName.isNotEmpty()) {
                    val newGroup = Group(groupNumber = groupNumber, facultyName = facultyName)
                    groupViewModel.addGroup(newGroup)
                } else {
                    Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    // метод для отображения диалога удаления группы
    private fun showDeleteGroupDialog(group: Group) {
        studentViewModel.filterByGroup(group.id).observe(viewLifecycleOwner) { students ->
            if (students.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Удалить группу?")
                    .setMessage("Вы уверены, что хотите удалить группу ${group.groupNumber}?")
                    .setPositiveButton("Да") { _, _ ->
                        groupViewModel.deleteGroup(group)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Невозможно удалить группу, в ней есть студенты", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // метод для навигации к фрагменту редактирования группы
    private fun navigateToEditGroupFragment(group: Group) {
        val action = GroupListFragmentDirections.actionGroupListFragmentToEditGroupFragment(group.id)
        findNavController().navigate(action)
    }
}

