package com.slots.app.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.slots.app.R
import com.slots.app.databinding.ItemDebtBinding
import com.slots.app.domain.model.Debt
import com.slots.app.domain.model.DebtStatus
import com.slots.app.domain.model.DebtType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DebtAdapter(
    private val onSettleClick: (Debt) -> Unit,
    private val onDeleteClick: (Debt) -> Unit
) : ListAdapter<Debt, DebtAdapter.DebtViewHolder>(DebtDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val binding = ItemDebtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DebtViewHolder(private val binding: ItemDebtBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(debt: Debt) {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            binding.tvPersonName.text = debt.personName
            binding.tvDebtAmount.text = currencyFormat.format(debt.amount)
            binding.tvDebtType.text = if (debt.type == DebtType.LENT) "You lent" else "You borrowed"
            binding.tvDebtDescription.text = debt.description.ifEmpty { "No notes" }
            binding.tvDebtDate.text = dateFormat.format(Date(debt.date))
            binding.tvDebtStatus.text = debt.status.name

            binding.tvDebtType.setTextColor(
                binding.root.context.getColor(
                    if (debt.type == DebtType.LENT) R.color.income_green else R.color.expense_red
                )
            )

            binding.tvDebtStatus.setTextColor(
                binding.root.context.getColor(
                    if (debt.status == DebtStatus.SETTLED) R.color.task_completed else R.color.task_pending
                )
            )

            binding.btnSettle.isEnabled = debt.status == DebtStatus.PENDING
            binding.btnSettle.setOnClickListener { onSettleClick(debt) }
            binding.btnDeleteDebt.setOnClickListener { onDeleteClick(debt) }
        }
    }

    class DebtDiffCallback : DiffUtil.ItemCallback<Debt>() {
        override fun areItemsTheSame(oldItem: Debt, newItem: Debt) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Debt, newItem: Debt) = oldItem == newItem
    }
}
