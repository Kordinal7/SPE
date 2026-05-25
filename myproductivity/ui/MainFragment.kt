package com.example.myproductivity.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproductivity.R
import com.example.myproductivity.databinding.FragmentMainBinding
import com.example.myproductivity.ui.adapter.TaskAdapter
import com.example.myproductivity.viewmodel.TaskViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = java.text.SimpleDateFormat("EEE, dd.MM", java.util.Locale("ru"))
        binding.tvDate.text = dateFormat.format(java.util.Date())

        adapter = TaskAdapter(
            onPlayClick = { task -> viewModel.toggleTask(task.id) },
            getTaskMillis = { id -> viewModel.taskMillis.value?.get(id) ?: 0L },
            getActiveTaskId = { viewModel.activeTaskId.value },
            formatTime = { millis -> viewModel.formatTime(millis) }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter

        viewModel.activeTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }

        viewModel.totalMillis.observe(viewLifecycleOwner) { millis ->
            binding.tvTotalTime.text = viewModel.formatTime(millis)
        }

        viewModel.taskMillis.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        viewModel.activeTaskId.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_edit)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}