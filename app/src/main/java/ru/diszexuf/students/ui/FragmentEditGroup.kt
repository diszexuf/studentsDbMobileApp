package ru.diszexuf.students.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.diszexuf.students.databinding.FragmentEditGroupBinding
import ru.diszexuf.students.viewmodel.GroupViewModel

@AndroidEntryPoint
class FragmentEditGroup : Fragment() {

    private var _binding: FragmentEditGroupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by viewModels()
    private var groupId: Long? = null // Для редактирования группы

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, если это редактирование, то получаем данные о группе
        arguments?.let { args ->
            groupId = args.getLong("groupId")
            groupId?.let {
                viewModel.getGroupById(it).observe(viewLifecycleOwner) { group ->
                    binding.groupNumberEditText.setText(group.groupNumber)
                    binding.facultyNameEditText.setText(group.facultyName)
                }
            }
        }

        binding.saveButton.setOnClickListener {
            val groupNumber = binding.groupNumberEditText.text.toString()
            val facultyName = binding.facultyNameEditText.text.toString()

            if (groupId == null) {
                // Добавляем новую группу
                val newGroup = Group(groupNumber = groupNumber, facultyName = facultyName)
                viewModel.insertGroup(newGroup)
            } else {
                // Редактируем существующую группу
                val updatedGroup = Group(id = groupId!!, groupNumber = groupNumber, facultyName = facultyName)
                viewModel.updateGroup(updatedGroup)
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

