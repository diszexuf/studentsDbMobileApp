package ru.diszexuf.students.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
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

    private val studentViewModel: StudentViewModel by viewModels() // viewmodel для работы со студентами
    private lateinit var studentAdapter: StudentAdapter // адаптер для recyclerview
    private lateinit var groupAdapter: ArrayAdapter<String> // адаптер для спиннера групп
    private lateinit var groupList: List<Group> // список групп
    private var currentGroupId: Long = 0L // текущий id группы для фильтрации

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // инициализация recyclerview и адаптера
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStudents)
        studentAdapter = StudentAdapter(
            onDeleteClick = { student -> showDeleteStudentDialog(student) },
            onEditClick = { student -> navigateToEditStudent(student) }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // инициализация спиннера для фильтрации по группам
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
                    currentGroupId = groupList[position].id
                    updateStudentList()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    currentGroupId = 0L
                    updateStudentList()
                }
            }
        }

        // наблюдение за изменениями в списке студентов и обновление адаптера
        studentViewModel.students.observe(viewLifecycleOwner) { students ->
            Log.d("StudentListFragment", "All students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
            studentAdapter.submitList(students)
        }

        // инициализация поля поиска
        val searchField = view.findViewById<EditText>(R.id.searchField)

        searchField.filters = arrayOf(
            InputFilter { source, start, end, dest, dstart, dend ->
                if (source.matches("[a-zA-Zа-яА-Я]+".toRegex())) {
                    null
                } else {
                    ""
                }
            }
        )

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()

                if (query.isEmpty()) {
                    updateStudentList()
                } else {
                    studentViewModel.searchByLastName(query).observe(viewLifecycleOwner) { students ->
                        Log.d("StudentListFragment", "Searched students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
                        studentAdapter.submitList(students)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        // обработка нажатия на кнопку добавления студента
        val addStudentButton = view.findViewById<FloatingActionButton>(R.id.addStudentButton)
        addStudentButton.setOnClickListener {
            navigateToEditStudentFragment(0L)
        }
    }

    // метод для обновления списка студентов в зависимости от текущей группы
    private fun updateStudentList() {
        studentViewModel.students.removeObservers(viewLifecycleOwner)

        if (currentGroupId != 0L) {
            studentViewModel.filterByGroup(currentGroupId).observe(viewLifecycleOwner) { students ->
                Log.d("StudentListFragment", "Filtered students by group: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
                studentAdapter.submitList(students)
            }
        } else {
            studentViewModel.students.observe(viewLifecycleOwner) { students ->
                Log.d("StudentListFragment", "All students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
                studentAdapter.submitList(students)
            }
        }
    }

    // метод для отображения диалога удаления студента
    private fun showDeleteStudentDialog(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить студента?")
            .setMessage("Вы уверены, что хотите удалить ${student.firstName} ${student.lastName}?")
            .setPositiveButton("Удалить") { _, _ ->
                studentViewModel.deleteStudent(student)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // метод для навигации к фрагменту редактирования студента
    private fun navigateToEditStudent(student: Student) {
        Log.d("StudentListFragment", "StudentID to edit ${student.id}")
        val action = StudentListFragmentDirections.actionStudentListFragmentToEditStudentFragment(student.id)
        findNavController().navigate(action)
    }

    // метод для навигации к фрагменту редактирования студента с заданным id
    private fun navigateToEditStudentFragment(studentId: Long) {
        Log.d("StudentListFragment", "StudentID to edit ${studentId}")
        val action = StudentListFragmentDirections.actionStudentListFragmentToEditStudentFragment(studentId)
        findNavController().navigate(action)
    }
}
