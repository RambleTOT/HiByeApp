package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.databinding.ItemParticipantBinding

class ParticipantsAdapter(
    private val participants: List<CreateUserResponse>,
    private val onItemClick: (CreateUserResponse) -> Unit // Лямбда для обработки нажатий
) : RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding = ItemParticipantBinding.inflate(
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

    override fun getItemCount(): Int = participants.size

    class ParticipantViewHolder(private val binding: ItemParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: CreateUserResponse) {
            binding.participantName.text = participant.userName
            binding.participantDescription.text = participant.userInfo

        }
    }
}