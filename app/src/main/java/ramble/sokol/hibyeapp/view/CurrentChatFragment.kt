package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.chat.ChatSendMessageEntity
import ramble.sokol.hibyeapp.databinding.FragmentCurrentChatBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.ChatMessagesAdapter
import ramble.sokol.hibyeapp.view_model.ChatViewModel
import ramble.sokol.hibyeapp.view_model.ChatViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrentChatFragment : Fragment() {

    private var binding: FragmentCurrentChatBinding? = null
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var chatMessagesAdapter: ChatMessagesAdapter

    private var eventId: Long = -1
    private var userId: Long = -1
    private var chatId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCurrentChatBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireActivity())
        eventId = tokenManager.getCurrentEventId() ?: -1
        userId = tokenManager.getUserIdTelegram() ?: -1
        chatId = arguments?.getLong("chatId", -1) ?: -1

        chatViewModel = ViewModelProvider(
            this,
            ChatViewModelFactory((requireActivity().application as MyApplication).chatRepository)
        ).get(ChatViewModel::class.java)

        chatMessagesAdapter = ChatMessagesAdapter(userId)
        binding?.recyclerViewMessages?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatMessagesAdapter
        }

        loadMessages()

        binding?.buttonSendMessage?.setOnClickListener {
            sendMessage()
        }

        lifecycleScope.launch {
            while (true) {
                delay(1500)
                loadMessages()
            }
        }

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding!!.iconBack.setOnClickListener {
            binding!!.iconBack.startAnimation(scaleDown)
            binding!!.iconBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val bottomNavigationFragment = BottomNavBarFragment(ChatsFragment())
            transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

    }

    private fun loadMessages() {
        chatViewModel.getAllChatMessage(eventId, userId, chatId)

        chatViewModel.getAllChatMessage.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val messages = result.getOrNull() ?: emptyList()
                chatMessagesAdapter.submitList(messages)
                binding?.recyclerViewMessages?.scrollToPosition(messages.size - 1)
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("CurrentChatFragment", "Error loading messages: ${exception?.message}")
            }
        })
    }

    private fun sendMessage() {
        val messageText = binding?.editTextMessage?.text?.toString() ?: return
        if (messageText.isBlank()) return

        val message = ChatSendMessageEntity(
            chatId = chatId,
            userId = userId,
            messageText = messageText,
            timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(
                Date()
            )
        )

        chatViewModel.sendMessage(eventId, message)
        chatViewModel.sendMessage.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                binding?.editTextMessage?.text?.clear() // Очистка поля ввода
                loadMessages() // Обновление списка сообщений
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("CurrentChatFragment", "Error sending message: ${exception?.message}")
            }
        })
    }

}