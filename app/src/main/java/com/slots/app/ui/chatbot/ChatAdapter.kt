package com.slots.app.ui.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.slots.app.databinding.ItemChatBotBinding
import com.slots.app.databinding.ItemChatUserBinding
import com.slots.app.domain.model.ChatMessage
import com.slots.app.domain.model.MessageRole

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_BOT = 1
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).role == MessageRole.USER) VIEW_TYPE_USER else VIEW_TYPE_BOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding = ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserViewHolder(binding)
        } else {
            val binding = ItemChatBotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            BotViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserViewHolder -> holder.bind(message)
            is BotViewHolder -> holder.bind(message)
        }
    }

    class UserViewHolder(private val binding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.tvUserMessage.text = message.content
        }
    }

    class BotViewHolder(private val binding: ItemChatBotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.tvBotMessage.text = message.content
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) = oldItem == newItem
    }
}
