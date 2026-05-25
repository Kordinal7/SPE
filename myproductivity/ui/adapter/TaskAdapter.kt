package com.example.myproductivity.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myproductivity.data.TaskEntity
import com.example.myproductivity.databinding.ItemTaskBinding

class TaskAdapter(
    private val onPlayClick: (TaskEntity) -> Unit,
    private val getTaskMillis: (Int) -> Long,
    private val getActiveTaskId: () -> Int?,
    private val formatTime: (Long) -> String
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(DiffCallback()) {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.tvTaskName.text = task.name
            binding.tvTaskTime.text = formatTime(getTaskMillis(task.id))

            // Цвет кнопки play = цвет задачи
            binding.btnPlay.backgroundTintList =
                android.content.res.ColorStateList.valueOf(task.color)

            val isActive = getActiveTaskId() == task.id
            binding.btnPlay.setImageResource(
                if (isActive) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play
            )

            binding.btnPlay.setOnClickListener { onPlayClick(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem == newItem
    }
}