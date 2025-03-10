package ramble.sokol.hibyeapp.view.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
        private val imageParticipant: ImageView = itemView.findViewById(R.id.image_participant_chat)

        fun bind(chat: ChatResponse) {
            chatName.text = chat.chatName
            if (chat.lastMessage == null){
                lastMessage.text = "Нет сообщений"
            }else {
                lastMessage.text = chat.lastMessage!!.messageText ?: "Нет сообщений"
            }
            if (!chat.chatPhoto.isNullOrEmpty()) {

                Glide.with(imageParticipant.context)
                    .load(chat.chatPhoto)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageParticipant.setImageResource(R.drawable.icon_profile)
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
                    .into(imageParticipant)
            } else {
                imageParticipant.setImageResource(R.drawable.icon_profile)
            }
        }
    }
}