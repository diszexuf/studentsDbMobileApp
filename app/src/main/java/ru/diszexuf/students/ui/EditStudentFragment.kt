package ru.diszexuf.students.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.R
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EditStudentFragment : Fragment(R.layout.fragment_edit_student) {

    private val studentViewModel: StudentViewModel by viewModels() // viewmodel для работы со студентами
    private var studentId: Long = 0 // id студента для редактирования

    private lateinit var firstNameInput: TextView
    private lateinit var lastNameInput: TextView
    private lateinit var patronymicInput: TextView
    private lateinit var birthDateInput: TextView
    private lateinit var groupSpinner: Spinner
    private lateinit var saveButton: Button

    private lateinit var groupAdapter: ArrayAdapter<String> // адаптер для спиннера групп

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

        // фильтр для полей ввода, разрешающий только буквы
        val letterFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches("[a-zA-Zа-яА-ЯёЁ]+".toRegex())) {
                null
            } else {
                ""
            }
        }

        firstNameInput.filters = arrayOf(letterFilter)
        lastNameInput.filters = arrayOf(letterFilter)
        patronymicInput.filters = arrayOf(letterFilter)

        // обработка нажатия на поле ввода для даты рождения
        birthDateInput.setOnClickListener {
            showDatePickerDialog()
        }

        // наблюдение за изменениями в списке групп
        studentViewModel.groups.observe(viewLifecycleOwner) { groups ->
            val groupNames = groups.map { it.groupNumber }
            groupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, groupNames)
            groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            groupSpinner.adapter = groupAdapter
        }

        // если редактируется существующий студент
        if (studentId != 0L) {
            studentViewModel.students.observe(viewLifecycleOwner) { students ->
                val student = students.find { it.id == studentId }
                if (student != null) {
                    firstNameInput.text = student.firstName
                    lastNameInput.text = student.lastName
                    patronymicInput.text = student.patronymic
                    birthDateInput.text = student.birthDate

                    val groupPosition = studentViewModel.groups.value?.indexOfFirst { it.id == student.groupId } ?: 0 // поиск позиции группы
                    groupSpinner.setSelection(groupPosition)
                }
            }
        }

        // обработка нажатия на кнопку сохранения
        saveButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val patronymic = patronymicInput.text.toString()
            val birthDate = birthDateInput.text.toString()
            val groupId = studentViewModel.groups.value?.get(groupSpinner.selectedItemPosition)?.id ?: 0

            val updatedStudent = Student(studentId, firstName, lastName, patronymic, birthDate, groupId)
            studentViewModel.updateStudent(updatedStudent) // обновление студента
            findNavController().popBackStack() // возврат к предыдущему фрагменту
        }
    }

    // метод для отображения диалога выбора даты
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.set(currentYear - 16, currentMonth, currentDay)
        val maxDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            birthDateInput.text = dateFormat.format(selectedDate.time)
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }

    companion object {
        // метод для создания нового экземпляра фрагмента с заданным id студента
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
