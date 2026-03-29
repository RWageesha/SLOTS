package com.slots.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.slots.app.R
import com.slots.app.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

        viewModel.pendingTasks.observe(viewLifecycleOwner) { tasks ->
            binding.tvPendingTaskCount.text = tasks.size.toString()
            binding.tvUpcomingDeadlines.text = tasks
                .filter { it.deadline != null }
                .take(3)
                .joinToString("\n") { "• ${it.title}" }
                .ifEmpty { getString(R.string.no_upcoming_deadlines) }
        }

        viewModel.monthlyIncome.observe(viewLifecycleOwner) { income ->
            binding.tvMonthlyIncome.text = currencyFormat.format(income ?: 0.0)
        }

        viewModel.monthlyExpenses.observe(viewLifecycleOwner) { expenses ->
            val expenseAmount = expenses ?: 0.0
            binding.tvMonthlyExpenses.text = currencyFormat.format(expenseAmount)
        }

        viewModel.monthlyIncome.observe(viewLifecycleOwner) { income ->
            viewModel.monthlyExpenses.value?.let { expenses ->
                val balance = (income ?: 0.0) - expenses
                binding.tvBalance.text = currencyFormat.format(balance)
            }
        }
    }

    private fun setupClickListeners() {
        binding.cardTasks.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_tasks)
        }
        binding.cardBudget.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_budget)
        }
        binding.cardDebts.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_debt)
        }
        binding.cardChat.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_chatbot)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
