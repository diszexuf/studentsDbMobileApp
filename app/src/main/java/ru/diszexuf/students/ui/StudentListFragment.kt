package ru.diszexuf.students.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.R
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.ui.adapters.StudentAdapter
import ru.diszexuf.students.viewmodel.StudentViewModel

@AndroidEntryPoint
class StudentListFragment : Fragment(R.layout.fragment_student_list) {

    private val studentViewModel: StudentViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var groupAdapter: ArrayAdapter<String>
    private lateinit var groupList: List<Group>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStudents)
        studentAdapter = StudentAdapter(
            onDeleteClick = { student -> showDeleteStudentDialog(student) },
            onEditClick = { student -> navigateToEditStudent(student) }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val groupFilter = view.findViewById<Spinner>(R.id.groupFilter)

        studentViewModel.groups.observe(viewLifecycleOwner) { groups ->
            groupList = groups

            groupAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                groups.map { it.groupNumber }
            )
            groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            groupFilter.adapter = groupAdapter

            groupFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val groupId = groupList[position].id
                    studentViewModel.filterByGroup(groupId)
                        .observe(viewLifecycleOwner) { students ->
                            Log.d("StudentListFragment", "Filtered students by group: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
                            studentAdapter.submitList(students)
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    studentViewModel.students.observe(viewLifecycleOwner) { students ->
                        Log.d("StudentListFragment", "All students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
                        studentAdapter.submitList(students)
                    }
                }
            }
        }

        // Наблюдаем за списком студентов (если фильтр по группе не активен)
        studentViewModel.students.observe(viewLifecycleOwner) { students ->
            // Логируем всех студентов
            Log.d("StudentListFragment", "All students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
            studentAdapter.submitList(students)  // Обновляем список студентов
        }

        // Поиск студентов по фамилии
        val searchField = view.findViewById<EditText>(R.id.searchField)
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()
                studentViewModel.searchByLastName(query).observe(viewLifecycleOwner) { students ->
                    // Логируем студентов после поиска по фамилии
                    Log.d("StudentListFragment", "Searched students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
                    studentAdapter.submitList(students) // Обновляем список студентов по поисковому запросу
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        // Обработчик нажатия на кнопку добавления студента
        val addStudentButton = view.findViewById<FloatingActionButton>(R.id.addStudentButton)
        addStudentButton.setOnClickListener {
            navigateToEditStudentFragment(0L) // Открываем фрагмент редактирования с пустыми полями для нового студента
        }
    }

    private fun showDeleteStudentDialog(student: Student) {
        // Покажите диалог подтверждения удаления студента
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить студента?")
            .setMessage("Вы уверены, что хотите удалить ${student.firstName} ${student.lastName}?")
            .setPositiveButton("Удалить") { _, _ ->
                studentViewModel.deleteStudent(student) // Удаляем студента
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun navigateToEditStudent(student: Student) {
        // Переход на экран редактирования студента
        Log.d("StudentListFragment", "StudentID to edit ${student.id}")
        val action = StudentListFragmentDirections.actionStudentListFragmentToEditStudentFragment(student.id)
        findNavController().navigate(action)
    }

    private fun navigateToEditStudentFragment(studentId: Long) {
        // Переход на экран добавления нового студента (с пустыми полями)
        Log.d("StudentListFragment", "StudentID to edit ${studentId}")
        val action = StudentListFragmentDirections.actionStudentListFragmentToEditStudentFragment(studentId)
        findNavController().navigate(action)
    }
}

