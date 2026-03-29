package com.slots.app.ui.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.slots.app.databinding.FragmentAddEditTaskBinding
import com.slots.app.domain.model.Task
import com.slots.app.domain.model.TaskCategory
import com.slots.app.domain.model.TaskPriority
import com.slots.app.domain.model.TaskStatus
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksViewModel by viewModels()
    private var selectedDeadline: Long? = null
    private var editingTaskId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingTaskId = arguments?.getLong("taskId", 0L) ?: 0L

        setupSpinners()
        setupClickListeners()

        if (editingTaskId > 0) {
            viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
                tasks.find { it.id == editingTaskId }?.let { populateFields(it) }
            }
        }
    }

    private fun setupSpinners() {
        val categories = TaskCategory.values().map { it.name }
        val priorities = TaskPriority.values().map { it.name }

        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, categories
        )
        binding.spinnerPriority.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, priorities
        )
    }

    private fun populateFields(task: Task) {
        binding.etTaskTitle.setText(task.title)
        binding.etTaskDescription.setText(task.description)
        binding.spinnerCategory.setSelection(TaskCategory.values().indexOf(task.category))
        binding.spinnerPriority.setSelection(TaskPriority.values().indexOf(task.priority))
        task.deadline?.let {
            selectedDeadline = it
            binding.tvDeadline.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        }
    }

    private fun setupClickListeners() {
        binding.btnPickDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDeadline = calendar.timeInMillis
                    binding.tvDeadline.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(selectedDeadline!!))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSave.setOnClickListener {
            saveTask()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.tilTaskTitle.error = "Title is required"
            return
        }
        binding.tilTaskTitle.error = null

        val task = Task(
            id = editingTaskId,
            title = title,
            description = binding.etTaskDescription.text.toString().trim(),
            category = TaskCategory.values()[binding.spinnerCategory.selectedItemPosition],
            priority = TaskPriority.values()[binding.spinnerPriority.selectedItemPosition],
            deadline = selectedDeadline,
            status = TaskStatus.PENDING
        )

        if (editingTaskId > 0) {
            viewModel.updateTask(task)
        } else {
            viewModel.insertTask(task)
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
