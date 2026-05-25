package com.example.myproductivity.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myproductivity.data.TaskEntity
import com.example.myproductivity.databinding.ItemEditTaskBinding
import com.example.myproductivity.R

class EditTaskAdapter(
    private val onMenuClick: (TaskEntity, View) -> Unit
) : ListAdapter<TaskEntity, EditTaskAdapter.EditTaskViewHolder>(DiffCallback()) {

    inner class EditTaskViewHolder(private val binding: ItemEditTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.tvTaskName.text = task.name
            val drawable = androidx.core.content.ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.circle_shape
            )?.mutate()
            drawable?.setTint(task.color)
            binding.viewColor.background = drawable
            binding.btnMenu.setOnClickListener { view ->
                onMenuClick(task, view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditTaskViewHolder {
        val binding = ItemEditTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EditTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem == newItem
    }
}