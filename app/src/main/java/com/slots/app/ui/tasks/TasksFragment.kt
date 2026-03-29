package com.slots.app.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.slots.app.R
import com.slots.app.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksViewModel by viewModels()
    private lateinit var adapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupFilters()
    }

    private fun setupRecyclerView() {
        adapter = TasksAdapter(
            onTaskClick = { task ->
                val bundle = Bundle().apply { putLong("taskId", task.id) }
                findNavController().navigate(R.id.action_tasks_to_addEditTask, bundle)
            },
            onToggleStatus = { task -> viewModel.toggleTaskStatus(task) },
            onDeleteClick = { task -> viewModel.deleteTask(task) }
        )
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@TasksFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            binding.tvEmptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_tasks_to_addEditTask)
        }
    }

    private fun setupFilters() {
        binding.chipAll.setOnClickListener {
            viewModel.setFilterCategory(null)
            viewModel.setFilterStatus(null)
        }
        binding.chipPending.setOnClickListener {
            viewModel.setFilterStatus("PENDING")
            viewModel.setFilterCategory(null)
        }
        binding.chipCompleted.setOnClickListener {
            viewModel.setFilterStatus("COMPLETED")
            viewModel.setFilterCategory(null)
        }
        binding.chipWork.setOnClickListener {
            viewModel.setFilterCategory("WORK")
            viewModel.setFilterStatus(null)
        }
        binding.chipStudy.setOnClickListener {
            viewModel.setFilterCategory("STUDY")
            viewModel.setFilterStatus(null)
        }
        binding.chipPersonal.setOnClickListener {
            viewModel.setFilterCategory("PERSONAL")
            viewModel.setFilterStatus(null)
        }
        binding.chipHealth.setOnClickListener {
            viewModel.setFilterCategory("HEALTH")
            viewModel.setFilterStatus(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
