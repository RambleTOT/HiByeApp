package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R

class ParticipantsDialogAdapter(
    private val userNames: List<String>,
    private val userDescriptions: List<String>
) : RecyclerView.Adapter<ParticipantsDialogAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant_all_dialog, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(userNames[position], userDescriptions[position])
    }

    override fun getItemCount(): Int {
        return userNames.size
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val participantName: TextView = itemView.findViewById(R.id.participant_name)
        private val participantDescription: TextView = itemView.findViewById(R.id.participant_description)

        fun bind(name: String, description: String) {
            participantName.text = name
            participantDescription.text = description
        }
    }
}