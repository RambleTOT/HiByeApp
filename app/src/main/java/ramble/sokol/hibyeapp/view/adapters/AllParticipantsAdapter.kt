package ramble.sokol.hibyeapp.view.adapters


import android.graphics.drawable.Drawable // Правильный импорт Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.databinding.ItemParticipantAllBinding

class AllParticipantsAdapter(
    private var participants: List<CreateUserResponse>,
    private val onItemClick: (CreateUserResponse) -> Unit // Лямбда для обработки нажатий
) : RecyclerView.Adapter<AllParticipantsAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding = ItemParticipantAllBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = participants[position]
        holder.bind(participant)
        holder.itemView.setOnClickListener {
            onItemClick(participant)
        }
    }

    fun updateData(newParticipants: List<CreateUserResponse>) {
        participants = newParticipants
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = participants.size

    class ParticipantViewHolder(private val binding: ItemParticipantAllBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: CreateUserResponse) {
            binding.participantName.text = participant.userName
            binding.participantDescription.text = participant.userInfo
            if (!participant.photoLink.isNullOrEmpty()) {

                Glide.with(binding.imageParticipant.context)
                    .load(participant.photoLink)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.imageParticipant.setImageResource(R.drawable.icon_profile)
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
                    .into(binding.imageParticipant)
            } else {
                binding.imageParticipant.setImageResource(R.drawable.icon_profile)
            }
        }
    }
}