package ru.diszexuf.students.ui

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.R
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.viewmodel.GroupViewModel

@AndroidEntryPoint
class EditGroupFragment : Fragment(R.layout.fragment_edit_group) {

    private val groupViewModel: GroupViewModel by viewModels() // viewmodel для работы с группами

    private var groupId: Long = 0L // id группы для редактирования

    private lateinit var groupNumberInput: EditText // поле ввода для номера группы
    private lateinit var facultyNameInput: EditText // поле ввода для названия факультета
    private lateinit var saveButton: Button // кнопка сохранения

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = arguments?.getLong("groupId", 0L) ?: 0L // получение id группы из аргументов

        groupNumberInput = view.findViewById(R.id.groupNumberInput) // инициализация поля ввода для номера группы
        facultyNameInput = view.findViewById(R.id.facultyNameInput) // инициализация поля ввода для названия факультета
        saveButton = view.findViewById(R.id.saveButton) // инициализация кнопки сохранения

        // фильтр для поля ввода названия факультета, разрешающий только буквы
        facultyNameInput.filters = arrayOf(
            InputFilter { source, start, end, dest, dstart, dend ->
                if (source.matches("[a-zA-Zа-яА-Я]+".toRegex())) {
                    null
                } else {
                    ""
                }
            }
        )

        // наблюдение за изменениями в списке групп
        groupViewModel.groups.observe(viewLifecycleOwner) { groups ->
            val group = groups.find { it.id == groupId } // поиск группы по id
            group?.let {
                groupNumberInput.setText(it.groupNumber) // установка номера группы
                facultyNameInput.setText(it.facultyName) // установка названия факультета
            }
        }

        // обработка нажатия на кнопку сохранения
        saveButton.setOnClickListener {
            val groupNumber = groupNumberInput.text.toString().trim() // получение номера группы
            val facultyName = facultyNameInput.text.toString().trim() // получение названия факультета

            if (groupNumber.isNotEmpty() && facultyName.isNotEmpty()) { // проверка заполненности полей
                val updatedGroup = Group(
                    id = groupId,
                    groupNumber = groupNumber,
                    facultyName = facultyName
                )
                groupViewModel.updateGroup(updatedGroup) // обновление группы
                findNavController().popBackStack() // возврат к предыдущему фрагменту
            } else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show() // отображение сообщения об ошибке
            }
        }
    }

    companion object {
        // метод для создания нового экземпляра фрагмента с заданным id группы
        fun newInstance(groupId: Long): EditGroupFragment {
            val args = Bundle().apply {
                putLong("groupId", groupId)
            }
            return EditGroupFragment().apply { arguments = args }
        }
    }
}
