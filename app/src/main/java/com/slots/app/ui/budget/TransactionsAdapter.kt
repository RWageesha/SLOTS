package com.slots.app.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.slots.app.R
import com.slots.app.databinding.ItemTransactionBinding
import com.slots.app.domain.model.Transaction
import com.slots.app.domain.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionsAdapter(
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

            binding.tvTransactionDescription.text =
                transaction.description.ifEmpty { transaction.category.name }
            binding.tvTransactionCategory.text = transaction.category.name
            binding.tvTransactionDate.text = dateFormat.format(Date(transaction.date))
            binding.tvTransactionAmount.text = if (transaction.type == TransactionType.INCOME) {
                "+${currencyFormat.format(transaction.amount)}"
            } else {
                "-${currencyFormat.format(transaction.amount)}"
            }
            binding.tvTransactionAmount.setTextColor(
                binding.root.context.getColor(
                    if (transaction.type == TransactionType.INCOME) R.color.income_green
                    else R.color.expense_red
                )
            )
            binding.btnDeleteTransaction.setOnClickListener { onDeleteClick(transaction) }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
    }
}
