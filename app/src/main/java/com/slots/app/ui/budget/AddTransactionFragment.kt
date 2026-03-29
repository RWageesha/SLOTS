package com.slots.app.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.slots.app.databinding.FragmentAddTransactionBinding
import com.slots.app.domain.model.Transaction
import com.slots.app.domain.model.TransactionCategory
import com.slots.app.domain.model.TransactionType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupClickListeners()
    }

    private fun setupSpinners() {
        val categories = TransactionCategory.values().map { it.name }
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, categories
        )
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveTransaction() {
        val amountStr = binding.etAmount.text.toString().trim()
        if (amountStr.isEmpty()) {
            binding.tilAmount.error = "Amount is required"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Enter a valid amount"
            return
        }
        binding.tilAmount.error = null

        val type = if (binding.rgType.checkedRadioButtonId == binding.rbIncome.id) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }

        val transaction = Transaction(
            type = type,
            amount = amount,
            category = TransactionCategory.values()[binding.spinnerCategory.selectedItemPosition],
            description = binding.etDescription.text.toString().trim()
        )

        viewModel.insertTransaction(transaction)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
