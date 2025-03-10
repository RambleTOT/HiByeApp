package ramble.sokol.hibyeapp.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Job
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
    private var name: String = ""
    private var chatPhoto: String = ""
    private var lastVisiblePosition: Int = 0

    private var messagesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCurrentChatBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.layout_fragment, BottomNavBarFragment(ChatsFragment()))
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        })

        tokenManager = TokenManager(requireActivity())
        eventId = tokenManager.getCurrentEventId() ?: -1
        userId = tokenManager.getUserIdTelegram() ?: -1
        chatId = arguments?.getLong("chatId", -1) ?: -1
        name = arguments?.getString("chatName", "") ?: ""
        chatPhoto = arguments?.getString("chatPhoto", "") ?: ""

        if (!chatPhoto.isNullOrEmpty()) {

            Glide.with(binding!!.imageParticipant.context)
                .load(chatPhoto)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.imageParticipant.setImageResource(R.drawable.icon_profile)
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }
                })
                .into(binding!!.imageParticipant)
        } else {
            binding!!.imageParticipant.setImageResource(R.drawable.icon_profile)
        }

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

        binding!!.chatName.text = name

        binding?.buttonSendMessage?.setOnClickListener {
            sendMessage()
        }

        messagesJob = lifecycleScope.launch {
            while (true) {
                delay(1500)
                if (isAdded && view != null) {
                    loadMessages()
                }
            }
        }

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding?.iconBack?.setOnClickListener {
            it.startAnimation(scaleDown)
            it.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val registrationFragment = BottomNavBarFragment(ChatsFragment())
            transaction.replace(R.id.layout_fragment, registrationFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }

    private fun loadMessages() {
        if (!isAdded || view == null) return // Проверяем, что фрагмент добавлен и View существует

        // Сохраняем текущую позицию прокрутки
        val layoutManager = binding?.recyclerViewMessages?.layoutManager as LinearLayoutManager
        lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        chatViewModel.getAllChatMessage(eventId, userId, chatId)

        chatViewModel.getAllChatMessage.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val messages = result.getOrNull() ?: emptyList()
                val previousSize = chatMessagesAdapter.itemCount
                chatMessagesAdapter.submitList(messages) {
                    // Восстанавливаем позицию прокрутки только если пользователь не прокрутил вниз
                    if (layoutManager.findLastCompletelyVisibleItemPosition() != previousSize - 1) {
                        layoutManager.scrollToPosition(lastVisiblePosition)
                    }
                }
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
                loadMessages() // Обновление списка сообщений без прокрутки вниз
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("CurrentChatFragment", "Error sending message: ${exception?.message}")
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Останавливаем корутину при уничтожении View
        messagesJob?.cancel()
        binding = null
    }
}