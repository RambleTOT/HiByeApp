package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.chat.ChatResponse
import ramble.sokol.hibyeapp.databinding.FragmentChatsBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.ChatsAdapter
import ramble.sokol.hibyeapp.view_model.ChatViewModel
import ramble.sokol.hibyeapp.view_model.ChatViewModelFactory

class ChatsFragment : Fragment() {

    private var binding: FragmentChatsBinding? = null
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userId = tokenManager.getUserIdTelegram()
        chatViewModel = ViewModelProvider(
            this,
            ChatViewModelFactory((requireActivity().application as MyApplication).chatRepository)
        ).get(ChatViewModel::class.java)

        chatsAdapter = ChatsAdapter(emptyList()) { chat ->
            navigateToChattDetails(chat)
        }

        binding?.recyclerView?.apply {
            layoutManager = NonScrollLinearLayoutManager(requireContext())
            adapter = chatsAdapter
            isNestedScrollingEnabled = false
        }

        chatViewModel.getAllChat(eventId!!, userId!!)
        chatViewModel.getAllChat.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val chats = result.getOrNull() ?: emptyList()
                chatsAdapter.updateChats(chats)
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        lifecycleScope.launch {
            while (true) {
                delay(2000)
                chatViewModel.getAllChat(eventId, userId)
            }
        }

    }

    private fun navigateToChattDetails(chat: ChatResponse) {

        val bundle = Bundle().apply {
            putLong("chatId", chat.chatId ?: -1)
            putString("chatName", chat.chatName)
            putString("chatPhoto", chat.chatPhoto)
        }

        val participantDetailsFragment = CurrentChatFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, participantDetailsFragment)
            addToBackStack(null)
            commit()
        }

    }

}