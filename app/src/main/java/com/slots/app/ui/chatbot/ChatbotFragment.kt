package com.slots.app.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.slots.app.R
import com.slots.app.databinding.FragmentChatbotBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatbotViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupMenu()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
            adapter = chatAdapter
        }
    }

    private fun setupObservers() {
        viewModel.chatHistory.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                if (messages.isNotEmpty()) {
                    binding.recyclerViewChat.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBarChat.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.btnSend.isEnabled = !isLoading
                    }
                }
                launch {
                    viewModel.errorMessage.collect { error ->
                        error?.let {
                            com.google.android.material.snackbar.Snackbar.make(
                                binding.root, it, com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.chat_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (menuItem.itemId == R.id.action_clear_chat) {
                    viewModel.clearHistory()
                    true
                } else false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
