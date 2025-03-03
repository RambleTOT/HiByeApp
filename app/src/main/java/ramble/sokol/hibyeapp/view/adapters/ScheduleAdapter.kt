package ramble.sokol.hibyeapp.view.adapters

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleItem

class ScheduleAdapter(
    private val items: List<ScheduleItem>,
    private val onItemClick: (ScheduleItem) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    var currentList: List<ScheduleItem> = items

    fun submitList(newList: List<ScheduleItem>) {
        currentList = newList
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

        @SuppressLint("MissingInflatedId")
        fun bind(item: ScheduleItem) {

            scheduleName.text = item.title

            val startTime = item.timeStart?.substring(11, 16)
            val endTime = item.timeEnd?.substring(11, 16)
            scheduleTime.text = "$startTime - $endTime"

            // Получаем массив цветов
            val tagColors: TypedArray = itemView.context.resources.obtainTypedArray(R.array.tag_colors)

            // Добавляем каждый тег
            item.tags?.forEachIndexed { index, tag ->
                val tagView = LayoutInflater.from(itemView.context).inflate(R.layout.item_tag, layoutTags, false)
                val tagText = tagView.findViewById<TextView>(R.id.tag_text)
                val cardView = tagView.findViewById<androidx.cardview.widget.CardView>(R.id.card_view)

                // Устанавливаем текст тега
                tagText.text = tag

                // Устанавливаем цвет для CardView
                val color = tagColors.getColor(index % tagColors.length(), itemView.context.getColor(R.color.main_color))
                cardView.setCardBackgroundColor(color)

                // Добавляем тег в контейнер
                layoutTags.addView(tagView)
            }

            // Освобождаем ресурсы TypedArray
            tagColors.recycle()
        }
    }
}