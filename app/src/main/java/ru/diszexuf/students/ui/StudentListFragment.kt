package ru.diszexuf.students.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.R
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.data.entities.Student
import ru.diszexuf.students.viewmodel.StudentViewModel

@AndroidEntryPoint
class StudentListFragment : Fragment(R.layout.fragment_student_list) {

    private val studentViewModel: StudentViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var groupAdapter: ArrayAdapter<String>  // Адаптер для строк (номеров групп)
    private lateinit var groupList: List<Group>  // Список групп

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStudents)
        studentAdapter = StudentAdapter { student -> showDeleteStudentDialog(student) } // Передаем обработчик для удаления
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Настройка Spinner для фильтрации по группе
        val groupFilter = view.findViewById<Spinner>(R.id.groupFilter)

        // Наблюдаем за группами
        studentViewModel.groups.observe(viewLifecycleOwner) { groups ->
            groupList = groups // Сохраняем список групп

            // Настроим адаптер для отображения номеров групп
            groupAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                groups.map { it.groupNumber } // Отображаем только номера групп
            )
            groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            groupFilter.adapter = groupAdapter

            // Настроим обработчик выбора группы
            groupFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val groupId = groupList[position].id // Получаем ID выбранной группы
                    studentViewModel.filterByGroup(groupId)
                        .observe(viewLifecycleOwner) { students ->
                            studentAdapter.submitList(students) // Обновляем список студентов
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    studentViewModel.students.observe(viewLifecycleOwner) { students ->
                        studentAdapter.submitList(students) // Если ничего не выбрано, показываем всех студентов
                    }
                }
            }
        }

        // Наблюдаем за списком студентов (если фильтр по группе не активен)
        studentViewModel.students.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)  // Обновляем список студентов
        }

        // Поиск студентов по фамилии
        val searchField = view.findViewById<EditText>(R.id.searchField)
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()
                studentViewModel.searchByLastName(query).observe(viewLifecycleOwner) { students ->
                    studentAdapter.submitList(students) // Обновляем список студентов по поисковому запросу
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
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
}



