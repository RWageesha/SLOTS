package com.slots.app.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.slots.app.databinding.FragmentDebtBinding
import com.slots.app.databinding.FragmentAddTransactionBinding
import com.slots.app.domain.model.Debt
import com.slots.app.domain.model.DebtType
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DebtFragment : Fragment() {

    private var _binding: FragmentDebtBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DebtViewModel by viewModels()
    private lateinit var adapter: DebtAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = DebtAdapter(
            onSettleClick = { debt -> viewModel.settleDebt(debt) },
            onDeleteClick = { debt -> viewModel.deleteDebt(debt) }
        )
        binding.recyclerViewDebts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@DebtFragment.adapter
        }
    }

    private fun setupObservers() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        viewModel.allDebts.observe(viewLifecycleOwner) { debts ->
            adapter.submitList(debts)
            binding.tvEmptyDebts.visibility = if (debts.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.totalLent.observe(viewLifecycleOwner) { amount ->
            binding.tvTotalLent.text = currencyFormat.format(amount ?: 0.0)
        }
        viewModel.totalBorrowed.observe(viewLifecycleOwner) { amount ->
            binding.tvTotalBorrowed.text = currencyFormat.format(amount ?: 0.0)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddDebt.setOnClickListener {
            showAddDebtDialog()
        }
    }

    private fun showAddDebtDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.slots.app.R.layout.dialog_add_debt, null)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Debt")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val personName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    com.slots.app.R.id.et_person_name
                )?.text.toString().trim()
                val amountStr = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    com.slots.app.R.id.et_debt_amount
                )?.text.toString().trim()
                val description = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    com.slots.app.R.id.et_debt_description
                )?.text.toString().trim()
                val typeGroup = dialogView.findViewById<android.widget.RadioGroup>(com.slots.app.R.id.rg_debt_type)
                val debtType = if (typeGroup.checkedRadioButtonId == com.slots.app.R.id.rb_borrowed) {
                    DebtType.BORROWED
                } else {
                    DebtType.LENT
                }

                val amount = amountStr.toDoubleOrNull()
                if (personName.isNotEmpty() && amount != null && amount > 0) {
                    viewModel.insertDebt(
                        Debt(
                            personName = personName,
                            amount = amount,
                            type = debtType,
                            description = description
                        )
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
