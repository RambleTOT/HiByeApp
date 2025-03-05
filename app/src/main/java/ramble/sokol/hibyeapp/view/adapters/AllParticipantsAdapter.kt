package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.databinding.ItemParticipantAllBinding
import ramble.sokol.hibyeapp.databinding.ItemParticipantBinding

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

        }
    }
}