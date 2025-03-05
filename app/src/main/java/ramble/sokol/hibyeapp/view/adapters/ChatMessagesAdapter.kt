package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
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

        // Настройка отступов
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        // Отступ сверху для первого элемента
        if (position == 0) {
            layoutParams.topMargin = 16.dpToPx(holder.itemView.context)
        } else {
            layoutParams.topMargin = 12.dpToPx(holder.itemView.context) // Отступ между сообщениями
        }

        if (position == itemCount - 2) {
            val previousLastItemView = (holder.itemView.parent as? ViewGroup)?.getChildAt(position)
            previousLastItemView?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = 0
            }
        }

        if (position == itemCount - 1) {
            layoutParams.bottomMargin = 16.dpToPx(holder.itemView.context)
        } else {
            layoutParams.bottomMargin = 0
        }

        holder.itemView.layoutParams = layoutParams
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
            messageTime.text = formatTime(message.timestamp.toString())

            // Настройка отступов и выравнивания
            if (getItemViewType(adapterPosition) == VIEW_TYPE_MY_MESSAGE) {
                // Свои сообщения выравниваем справа
                itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginEnd = 16.dpToPx(itemView.context) // Отступ от правого края
                    marginStart = 16.dpToPx(itemView.context) // Отступ от левого края
                }
                // Выравниваем содержимое сообщения справа
                (messageText.parent as? LinearLayout)?.apply {
                    (messageText.layoutParams as? LinearLayout.LayoutParams)?.gravity = android.view.Gravity.END
                    (messageTime.layoutParams as? LinearLayout.LayoutParams)?.gravity = android.view.Gravity.END
                }
            } else {
                // Чужие сообщения выравниваем слева
                itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = 16.dpToPx(itemView.context) // Отступ от левого края
                    marginEnd = 16.dpToPx(itemView.context) // Отступ от правого края
                }
                // Выравниваем содержимое сообщения слева
                (messageText.parent as? LinearLayout)?.apply {
                    (messageText.layoutParams as? LinearLayout.LayoutParams)?.gravity = android.view.Gravity.START
                    (messageTime.layoutParams as? LinearLayout.LayoutParams)?.gravity = android.view.Gravity.START
                }
            }
        }

        private fun formatTime(timestamp: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val date = inputFormat.parse(timestamp) ?: return ""
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return outputFormat.format(date)
        }
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

        // Функция для конвертации dp в пиксели
        private fun Int.dpToPx(context: android.content.Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }
    }
}