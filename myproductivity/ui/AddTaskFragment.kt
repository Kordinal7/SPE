package com.example.myproductivity.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myproductivity.data.TaskEntity
import com.example.myproductivity.databinding.FragmentAddTaskBinding
import com.example.myproductivity.viewmodel.TaskViewModel

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()

    private var selectedColor: Int = Color.parseColor("#FF6B6B")

    private val colors = listOf(
        "#FF6B6B", "#FF9F43", "#FECA57",
        "#48DBFB", "#FF9FF3", "#54A0FF",
        "#5F27CD", "#1DD1A1"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPicker()

        binding.btnSave.setOnClickListener {
            val name = binding.etTaskName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.insertTask(TaskEntity(name = name, color = selectedColor))
            findNavController().popBackStack()
        }
    }

    private fun setupColorPicker() {
        colors.forEach { colorHex ->
            val colorView = View(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(80, 80).apply {
                    marginEnd = 16
                }
                setBackgroundResource(com.example.myproductivity.R.drawable.circle_shape)
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    Color.parseColor(colorHex)
                )
                setOnClickListener {
                    selectedColor = Color.parseColor(colorHex)
                    updateColorSelection(this)
                }
            }
            binding.colorPicker.addView(colorView)
        }

        // Выделить первый цвет по умолчанию
        val firstView = binding.colorPicker.getChildAt(0)
        firstView?.scaleX = 1.3f
        firstView?.scaleY = 1.3f
    }

    private fun updateColorSelection(selectedView: View) {
        for (i in 0 until binding.colorPicker.childCount) {
            val child = binding.colorPicker.getChildAt(i)
            child.scaleX = 1.0f
            child.scaleY = 1.0f
        }
        selectedView.scaleX = 1.3f
        selectedView.scaleY = 1.3f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}