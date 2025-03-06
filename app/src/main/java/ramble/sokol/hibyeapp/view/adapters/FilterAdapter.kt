package ramble.sokol.hibyeapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R

class FilterAdapter(
    private val tags: List<String>,
    private val selectedTags: List<String>, // Передаем выбранные теги
    private val onTagSelected: (List<String>) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private val selectedTagsSet = selectedTags.toMutableSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_tag, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val tag = tags[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int = tags.size

    fun getSelectedTags(): List<String> = selectedTagsSet.toList()

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_tag)

        fun bind(tag: String) {
            checkBox.text = tag
            checkBox.isChecked = selectedTagsSet.contains(tag)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTagsSet.add(tag)
                } else {
                    selectedTagsSet.remove(tag)
                }
                onTagSelected(selectedTagsSet.toList())
            }
        }
    }
}