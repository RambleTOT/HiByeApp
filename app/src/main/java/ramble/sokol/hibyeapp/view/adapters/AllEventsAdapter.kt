package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.managers.TokenManager

class AllEventsAdapter(
    private val events: List<EventsEntity>,
    private val onItemClick: (EventsEntity) -> Unit
) : RecyclerView.Adapter<AllEventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        val scaleDown = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.text_click_anim_back)
        val tokenManager = TokenManager(holder.itemView.context)
        holder.itemView.setOnClickListener {
            holder.itemView.startAnimation(scaleDown)
            holder.itemView.startAnimation(scaleUp)
            onItemClick(event)
            tokenManager.saveCurrentEvent(event.eventId!!.toLong())
            tokenManager.saveCurrentScheduleId(event.scheduleId!!.toLong())
            tokenManager.saveEventName(event.eventName!!)
        }
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(event: EventsEntity) {
            textView.text = event.eventName ?: "Unnamed Event"
        }
    }
}