package com.example.myproductivity.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproductivity.R
import com.example.myproductivity.databinding.FragmentEditTasksBinding
import com.example.myproductivity.ui.adapter.EditTaskAdapter
import com.example.myproductivity.viewmodel.TaskViewModel

class EditTasksFragment : Fragment() {

    private var _binding: FragmentEditTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: EditTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setPadding(0, 0, 0, 0)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        adapter = EditTaskAdapter { task, anchorView ->
            val popup = PopupMenu(requireContext(), anchorView)
            popup.menu.add("Удалить")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Удалить" -> viewModel.deleteTask(task)
                }
                true
            }
            popup.show()
        }

        binding.rvEditTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEditTasks.adapter = adapter

        viewModel.activeTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            binding.tvActiveCount.text = "Используется ${tasks.size}"
        }

        binding.btnAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_edit_to_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}