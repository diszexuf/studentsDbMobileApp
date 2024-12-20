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
import ru.diszexuf.students.data.entities.Group
import ru.diszexuf.students.databinding.FragmentGroupListBinding
import ru.diszexuf.students.viewmodel.GroupViewModel

@AndroidEntryPoint
class FragmentGroupList : Fragment() {

    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupAdapter(
            onGroupClick = { groupItem ->
                val group = Group(groupItem.id, groupItem.groupNumber, groupItem.facultyName)
            },
            onDeleteClick = { groupItem ->
                val group = Group(groupItem.id, groupItem.groupNumber, groupItem.facultyName)
                viewModel.deleteGroup(group)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.groups.collect { groups ->
                adapter.submitList(groups.map { GroupItem(it.id, it.groupNumber, it.facultyName) })
            }
        }

        binding.addButton.setOnClickListener {
            // Реализовать навигацию или логику добавления новой группы
            // Например, использовать NavController для перехода:
            // findNavController().navigate(R.id.action_to_addGroupFragment)
        }

        // Загружаем данные групп
        viewModel.loadGroups()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
