package com.slots.app.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.slots.app.R
import com.slots.app.databinding.ItemTaskBinding
import com.slots.app.domain.model.Task
import com.slots.app.domain.model.TaskPriority
import com.slots.app.domain.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TasksAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onToggleStatus: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTaskTitle.text = task.title
            binding.tvTaskCategory.text = task.category.name
            binding.tvTaskDescription.text = task.description.ifEmpty { "No description" }

            binding.tvTaskDeadline.text = task.deadline?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
            } ?: "No deadline"

            val priorityColor = when (task.priority) {
                TaskPriority.HIGH -> binding.root.context.getColor(R.color.expense_red)
                TaskPriority.MEDIUM -> binding.root.context.getColor(R.color.task_pending)
                TaskPriority.LOW -> binding.root.context.getColor(R.color.task_completed)
            }
            binding.viewPriorityIndicator.setBackgroundColor(priorityColor)
            binding.tvTaskPriority.text = task.priority.name

            binding.cbTaskStatus.isChecked = task.status == TaskStatus.COMPLETED
            binding.cbTaskStatus.setOnCheckedChangeListener { _, _ -> onToggleStatus(task) }

            binding.root.setOnClickListener { onTaskClick(task) }
            binding.btnDelete.setOnClickListener { onDeleteClick(task) }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
