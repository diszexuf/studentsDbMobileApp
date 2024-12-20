package ru.diszexuf.students.ui

import android.os.Bundle
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

    private val groupViewModel: GroupViewModel by viewModels()

    private var groupId: Long = 0L

    private lateinit var groupNumberInput: EditText
    private lateinit var facultyNameInput: EditText
    private lateinit var saveButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ID группы из аргументов
        groupId = arguments?.getLong("groupId", 0L) ?: 0L

        groupNumberInput = view.findViewById(R.id.groupNumberInput)
        facultyNameInput = view.findViewById(R.id.facultyNameInput)
        saveButton = view.findViewById(R.id.saveButton)

        // Наблюдаем за списком групп, чтобы заполнить данные при редактировании
        groupViewModel.groups.observe(viewLifecycleOwner) { groups ->
            val group = groups.find { it.id == groupId }
            group?.let {
                groupNumberInput.setText(it.groupNumber)
                facultyNameInput.setText(it.facultyName)
            }
        }

        saveButton.setOnClickListener {
            val groupNumber = groupNumberInput.text.toString().trim()
            val facultyName = facultyNameInput.text.toString().trim()

            if (groupNumber.isNotEmpty() && facultyName.isNotEmpty()) {
                val updatedGroup = Group(
                    id = groupId,
                    groupNumber = groupNumber,
                    facultyName = facultyName
                )
                groupViewModel.addGroup(updatedGroup) // Используем addGroup, так как Room заменяет записи с одинаковым ID
                findNavController().popBackStack() // Возврат к списку групп
            } else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(groupId: Long): EditGroupFragment {
            val args = Bundle().apply {
                putLong("groupId", groupId)
            }
            return EditGroupFragment().apply { arguments = args }
        }
    }
}
