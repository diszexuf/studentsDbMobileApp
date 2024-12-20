package ru.diszexuf.students.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.R
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.viewmodel.StudentViewModel

@AndroidEntryPoint
class EditStudentFragment : Fragment(R.layout.fragment_edit_student) {

    private val studentViewModel: StudentViewModel by viewModels()
    private var studentId: Long = 0

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var patronymicInput: EditText
    private lateinit var birthDateInput: EditText
    private lateinit var groupSpinner: Spinner
    private lateinit var saveButton: Button

    private lateinit var groupAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentId = arguments?.getLong("studentId", 0) ?: 0
        Log.d("EditStudentFragment", "Get studentId from parent: $studentId")
        firstNameInput = view.findViewById(R.id.firstNameInput)
        lastNameInput = view.findViewById(R.id.lastNameInput)
        patronymicInput = view.findViewById(R.id.patronymicInput)
        birthDateInput = view.findViewById(R.id.birthDateInput)
        groupSpinner = view.findViewById(R.id.groupSpinner)
        saveButton = view.findViewById(R.id.saveButton)

        studentViewModel.groups.observe(viewLifecycleOwner) { groups ->
            val groupNames = groups.map { it.groupNumber }
            groupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, groupNames)
            groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            groupSpinner.adapter = groupAdapter
        }

        if (studentId != 0L) {
            studentViewModel.students.observe(viewLifecycleOwner) { students ->
                val student = students.find { it.id == studentId }
                if (student != null) {
                    firstNameInput.setText(student.firstName)
                    lastNameInput.setText(student.lastName)
                    patronymicInput.setText(student.patronymic)
                    birthDateInput.setText(student.birthDate)

                    val groupPosition = studentViewModel.groups.value?.indexOfFirst { it.id == student.groupId } ?: 0
                    groupSpinner.setSelection(groupPosition)
                }
            }
        }

        saveButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val patronymic = patronymicInput.text.toString()
            val birthDate = birthDateInput.text.toString()
            val groupId = studentViewModel.groups.value?.get(groupSpinner.selectedItemPosition)?.id ?: 0

            val updatedStudent = Student(studentId, firstName, lastName, patronymic, birthDate, groupId)
            studentViewModel.updateStudent(updatedStudent)
            findNavController().popBackStack()
        }
    }

    companion object {
        fun newInstance(studentId: Long): EditStudentFragment {
            val args = Bundle().apply {
                putLong("studentId", studentId)
            }
            return EditStudentFragment().apply {
                arguments = args
            }
        }
    }
}

