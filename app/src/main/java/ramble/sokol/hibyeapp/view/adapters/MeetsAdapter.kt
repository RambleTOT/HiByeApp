package ramble.sokol.hibyeapp.view.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MeetsAdapter(
    private var meets: List<MeetingResponse>,
    private val onItemClick: (MeetingResponse) -> Unit,
    private val onGroupMeetClick: (MeetingResponse) -> Unit
) : RecyclerView.Adapter<MeetsAdapter.MeetViewHolder>() {

    fun updateData(newMeets: List<MeetingResponse>) {
        meets = newMeets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meets, parent, false)
        return MeetViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetViewHolder, position: Int) {
        val meet = meets[position]
        holder.bind(meet)
        holder.itemView.setOnClickListener {
            if (meet.meetingType == "REQUEST") {
                onItemClick(meet)
            } else if (meet.meetingType == "CUSTOM_MEETING") {
                onGroupMeetClick(meet)
            }
        }
    }

    override fun getItemCount(): Int = meets.size

    class MeetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val meetName: TextView = itemView.findViewById(R.id.meet_name)
        private val meetDescription: TextView = itemView.findViewById(R.id.meet_description)
        private val meetTime: TextView = itemView.findViewById(R.id.meet_time)
        private val imagePart: ImageView = itemView.findViewById(R.id.image_meet_part_item)
        private val imageGroup: FrameLayout = itemView.findViewById(R.id.image_group_meeting_item)
        private val textImageGroup: TextView = itemView.findViewById(R.id.count_meet_item)

        fun bind(meet: MeetingResponse) {
            meetName.text = meet.name
            meetDescription.text = meet.description
            if (meet.meetingType == "REQUEST") {
                meetTime.visibility = View.GONE
                imagePart.visibility = View.VISIBLE
                imageGroup.visibility = View.GONE
            } else {
                meetTime.text = formatDate(meet.timeStart)
                imagePart.visibility = View.GONE
                imageGroup.visibility = View.VISIBLE
                textImageGroup.text = "${meet.userIds!!.size} из ${meet.capacity}"
            }

        }

        private fun formatDate(dateString: String?): String {
            if (dateString == null) return ""

            val inputFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ISO_DATE_TIME
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val localDateTime = LocalDateTime.parse(dateString, inputFormatter)
            val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))
            val outputFormatter = DateTimeFormatter.ofPattern("'в' HH:mm")
            return zonedDateTime.format(outputFormatter)
        }
    }
}