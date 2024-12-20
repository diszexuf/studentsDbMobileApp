package ru.diszexuf.students.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.databinding.FragmentStudentListBinding
import ru.diszexuf.students.viewmodel.StudentViewModel

@AndroidEntryPoint
class FragmentStudentList : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = StudentAdapter(
            onStudentClick = { studentItem ->
                val student = Student(
                    id = studentItem.id,
                    firstName = studentItem.fullName.split(" ")[1],
                    lastName = studentItem.fullName.split(" ")[0],
                    patronymic = studentItem.fullName.split(" ")[2],
                    birthDate = studentItem.birthDate,
                    groupId = 0L // Указываем groupId или получаем из данных
                )
                // Логика для редактирования студента
            },
            onDeleteClick = { studentItem ->
                // Преобразуем StudentItem в Student для удаления
                val studentToDelete = Student(
                    id = studentItem.id,
                    firstName = studentItem.fullName.split(" ")[1],
                    lastName = studentItem.fullName.split(" ")[0],
                    patronymic = studentItem.fullName.split(" ")[2],
                    birthDate = studentItem.birthDate,
                    groupId = 0L // Указываем groupId или получаем из данных
                )
                // Вызываем метод удаления студента в ViewModel
                viewModel.deleteStudent(studentToDelete)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.students.collect { students ->
                adapter.submitList(
                    students.map { student ->
                        StudentItem(
                            id = student.id,
                            fullName = "${student.lastName} ${student.firstName} ${student.patronymic}",
                            birthDate = student.birthDate,
                            groupNumber = "Группа $student.groupId"
                        )
                    }
                )
            }
        }

        binding.addButton.setOnClickListener {
            // Навигация к экрану добавления нового студента
        }

        viewModel.loadStudents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
