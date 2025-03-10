package ramble.sokol.hibyeapp.view.adapters

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.media.Image
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
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleItem

class ScheduleAdapter(
    private var items: List<ScheduleItem>,
    private val onItemClick: (ScheduleItem) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    var currentList: List<ScheduleItem> = items

    fun submitList(newList: List<ScheduleItem>) {
        currentList = newList
        notifyDataSetChanged()
    }

    fun updateData(newItems: List<ScheduleItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meets_event, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scheduleName: TextView = itemView.findViewById(R.id.schedule_name)
        private val scheduleTime: TextView = itemView.findViewById(R.id.schedule_time)
        private val layoutTags: ViewGroup = itemView.findViewById(R.id.layout_tags)
        private val imageParticipant: ImageView = itemView.findViewById(R.id.image_participant)

        @SuppressLint("MissingInflatedId")
        fun bind(item: ScheduleItem) {
            layoutTags.removeAllViews()

            scheduleName.text = item.title

            val startTime = item.timeStart?.substring(11, 16)
            val endTime = item.timeEnd?.substring(11, 16)
            scheduleTime.text = "$startTime - $endTime"

            if (!item.imageUrl.isNullOrEmpty()) {

                Glide.with(imageParticipant.context)
                    .load(item.imageUrl)
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

            val tagColors: TypedArray = itemView.context.resources.obtainTypedArray(R.array.tag_colors)

            item.tags?.forEachIndexed { index, tag ->
                val tagView = LayoutInflater.from(itemView.context).inflate(R.layout.item_tag, layoutTags, false)
                val tagText = tagView.findViewById<TextView>(R.id.tag_text)
                val cardView = tagView.findViewById<androidx.cardview.widget.CardView>(R.id.card_view)

                tagText.text = tag

                val color = tagColors.getColor(index % tagColors.length(), itemView.context.getColor(R.color.main_color))
                cardView.setCardBackgroundColor(color)
                layoutTags.addView(tagView)
            }

            tagColors.recycle()
        }
    }
}