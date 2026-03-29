package com.slots.app.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.slots.app.R
import com.slots.app.databinding.FragmentBudgetBinding
import com.slots.app.domain.model.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChart()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = TransactionsAdapter(
            onDeleteClick = { transaction -> viewModel.deleteTransaction(transaction) }
        )
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@BudgetFragment.adapter
        }
    }

    private fun setupChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setUsePercentValues(true)
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
        }
    }

    private fun setupObservers() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

        viewModel.monthlyTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)

            val expensesByCategory = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category.name }
                .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }

            if (expensesByCategory.isNotEmpty()) {
                val entries = expensesByCategory.map { (cat, amount) -> PieEntry(amount, cat) }
                val dataSet = PieDataSet(entries, "Expenses").apply {
                    colors = listOf(
                        Color.parseColor("#F44336"), Color.parseColor("#E91E63"),
                        Color.parseColor("#9C27B0"), Color.parseColor("#3F51B5"),
                        Color.parseColor("#2196F3"), Color.parseColor("#00BCD4")
                    )
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                }
                binding.pieChart.data = PieData(dataSet)
                binding.pieChart.invalidate()
            }
        }

        viewModel.monthlyIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = currencyFormat.format(income ?: 0.0)
            updateBalance()
        }

        viewModel.monthlyExpenses.observe(viewLifecycleOwner) { expenses ->
            binding.tvTotalExpenses.text = currencyFormat.format(expenses ?: 0.0)
            updateBalance()
        }
    }

    private fun updateBalance() {
        val income = viewModel.monthlyIncome.value ?: 0.0
        val expenses = viewModel.monthlyExpenses.value ?: 0.0
        val balance = income - expenses
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        binding.tvBalance.text = currencyFormat.format(balance)
        binding.tvBalance.setTextColor(
            requireContext().getColor(if (balance >= 0) R.color.income_green else R.color.expense_red)
        )
    }

    private fun setupClickListeners() {
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_budget_to_addTransaction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
