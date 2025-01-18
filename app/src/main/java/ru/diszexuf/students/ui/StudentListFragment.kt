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

    private val studentViewModel: StudentViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var groupAdapter: ArrayAdapter<String>
    private lateinit var groupList: List<Group>
    private var currentGroupId: Long = 0L

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
                    currentGroupId = groupList[position].id
                    updateStudentList()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    currentGroupId = 0L
                    updateStudentList()
                }
            }
        }

        studentViewModel.students.observe(viewLifecycleOwner) { students ->
            Log.d("StudentListFragment", "All students: ${students.joinToString(", ") { it.firstName + " " + it.lastName }}")
            studentAdapter.submitList(students)
        }

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

        val addStudentButton = view.findViewById<FloatingActionButton>(R.id.addStudentButton)
        addStudentButton.setOnClickListener {
            navigateToEditStudentFragment(0L)
        }
    }

    private fun updateStudentList() {
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

    private fun navigateToEditStudent(student: Student) {
        Log.d("StudentListFragment", "StudentID to edit ${student.id}")
        val action = StudentListFragmentDirections.actionStudentListFragmentToEditStudentFragment(student.id)
        findNavController().navigate(action)
    }

    private fun navigateToEditStudentFragment(studentId: Long) {
        Log.d("StudentListFragment", "StudentID to edit ${studentId}")
        val action = StudentListFragmentDirections.actionStudentListFragmentToEditStudentFragment(studentId)
        findNavController().navigate(action)
    }
}
