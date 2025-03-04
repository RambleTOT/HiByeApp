package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.chat.ChatSendMessageEntity
import java.text.SimpleDateFormat
import java.util.*

class ChatMessagesAdapter(
    private val currentUserId: Long
) : ListAdapter<ChatSendMessageEntity, ChatMessagesAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == VIEW_TYPE_MY_MESSAGE) {
            R.layout.item_my_message
        } else {
            R.layout.item_foreign_message
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).userId == currentUserId) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_FOREIGN_MESSAGE
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.chat_message)
        private val messageTime: TextView = itemView.findViewById(R.id.chat_message_date)

        fun bind(message: ChatSendMessageEntity) {
            messageText.text = message.messageText
            messageTime.text = message.timestamp.toString()
        }

//        private fun formatTime(timestamp: String): String {
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//            val date = inputFormat.parse(timestamp) ?: return ""
//            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//            return outputFormat.format(date)
//        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<ChatSendMessageEntity>() {
        override fun areItemsTheSame(oldItem: ChatSendMessageEntity, newItem: ChatSendMessageEntity): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: ChatSendMessageEntity, newItem: ChatSendMessageEntity): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_FOREIGN_MESSAGE = 2
    }
}