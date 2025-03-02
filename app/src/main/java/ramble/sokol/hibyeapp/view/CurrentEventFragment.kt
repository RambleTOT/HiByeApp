package ramble.sokol.hibyeapp.view

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentCurrentEventBinding
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CurrentEventFragment : Fragment() {

    private var binding: FragmentCurrentEventBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCurrentEventBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

        val scheduleId = arguments?.getLong("scheduleId", -1) ?: -1
        val title = arguments?.getString("title", "") ?: ""
        val time = arguments?.getString("timeStart", "") ?: ""
        val timeEnd = arguments?.getString("timeEnd", "") ?: ""
        val description = arguments?.getString("description", "") ?: ""
        val tagsList = arguments?.getStringArrayList("tags") ?: ""
        val formattedTime = formatTime(time)
        val (date, timeFinal) = formattedTime!!

        binding!!.name.text = title
        binding!!.date.text = date
        binding!!.time.text = timeFinal
        binding!!.description.text = description

        val tags: Array<String> = if (arguments?.getSerializable("tags") is ArrayList<*>) {
            @Suppress("UNCHECKED_CAST")
            (arguments?.getSerializable("tags") as ArrayList<String>).toTypedArray()
        } else {
            emptyArray()
        }
        setupTags(tags)

        binding!!.textButtonBack.setOnClickListener {
            binding!!.textButtonBack.startAnimation(scaleDown)
            binding!!.textButtonBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val bottomNavigationFragment = BottomNavBarFragment(ScheduleFragment())
            transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

    }

    fun formatTime(time: String): Pair<String, String>? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            val date = inputFormat.parse(time)
            if (date != null) {
                val dateFormat = SimpleDateFormat("d MMMM yyyy г.", Locale.getDefault())
                val formattedDate = dateFormat.format(date)

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(date)

                Pair(formattedDate, formattedTime)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TimeFormat", "Failed to parse time: ${e.message}")
            null
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun setupTags(tags: Array<String>) {
        val layoutTags = binding?.layoutTags as? FlexboxLayout
        if (layoutTags == null) {
            Log.e("CurrentEventFragment", "layoutTags is null")
            return
        }

        // Очищаем контейнер перед добавлением новых тегов
        layoutTags.removeAllViews()

        // Получаем массив цветов
        val tagColors: TypedArray = resources.obtainTypedArray(R.array.tag_colors)

        // Добавляем каждый тег
        for ((index, tag) in tags.withIndex()) {
            val tagView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tag, layoutTags, false)

            val tagText = tagView.findViewById<TextView>(R.id.tag_text)
            tagText.text = tag

            val cardView = tagView.findViewById<androidx.cardview.widget.CardView>(R.id.card_view)
            val color = tagColors.getColor(index % tagColors.length(), Color.BLACK)
            cardView.setCardBackgroundColor(color)

            // Добавляем тег в контейнер
            layoutTags.addView(tagView)
        }

        // Освобождаем ресурсы TypedArray
        tagColors.recycle()
    }

}


//