package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.chat.ChatResponse

class ChatsAdapter(
    private var chats: List<ChatResponse>,
    private val onItemClick: (ChatResponse) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
        holder.itemView.setOnClickListener {
            onItemClick(chat)
        }
    }

    override fun getItemCount(): Int = chats.size

    fun updateChats(newChats: List<ChatResponse>) {
        chats = newChats
        notifyDataSetChanged()
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatName: TextView = itemView.findViewById(R.id.chat_name_item)
        private val lastMessage: TextView = itemView.findViewById(R.id.chat_last_message)

        fun bind(chat: ChatResponse) {
            chatName.text = chat.chatName
            if (chat.lastMessage == null){
                lastMessage.text = "Нет сообщений"
            }else {
                lastMessage.text = chat.lastMessage!!.messageText ?: "Нет сообщений"
            }
        }
    }
}