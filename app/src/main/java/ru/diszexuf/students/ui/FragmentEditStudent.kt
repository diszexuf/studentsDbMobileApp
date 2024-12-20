package ru.diszexuf.students.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.databinding.FragmentEditStudentBinding
import ru.diszexuf.students.viewmodel.StudentViewModel

@AndroidEntryPoint
class FragmentEditStudent : Fragment() {

    private var _binding: FragmentEditStudentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentViewModel by viewModels()
    private var studentId: Long? = null // Для редактирования студента

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, если это редактирование, то получаем данные о студенте
        arguments?.let { args ->
            studentId = args.getLong("studentId")
            studentId?.let {
                viewModel.getStudentById(it).observe(viewLifecycleOwner) { student ->
                    binding.firstNameEditText.setText(student.firstName)
                    binding.lastNameEditText.setText(student.lastName)
                    binding.patronymicEditText.setText(student.patronymic)
                    binding.birthDateEditText.setText(student.birthDate)
                    binding.groupIdEditText.setText(student.groupId.toString())
                }
            }
        }

        binding.saveButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val patronymic = binding.patronymicEditText.text.toString()
            val birthDate = binding.birthDateEditText.text.toString()
            val groupId = binding.groupIdEditText.text.toString().toLongOrNull() ?: 0L

            if (studentId == null) {
                // Добавляем нового студента
                val newStudent = Student(firstName = firstName, lastName = lastName, patronymic = patronymic, birthDate = birthDate, groupId = groupId)
                viewModel.insertStudent(newStudent)
            } else {
                // Редактируем существующего студента
                val updatedStudent = Student(id = studentId!!, firstName = firstName, lastName = lastName, patronymic = patronymic, birthDate = birthDate, groupId = groupId)
                viewModel.updateStudent(updatedStudent)
            }
            // Навигация назад или обновление UI
            findNavController().popBackStack()
        }

        binding.cancelButton.setOnClickListener {
            // Навигация назад без изменений
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

