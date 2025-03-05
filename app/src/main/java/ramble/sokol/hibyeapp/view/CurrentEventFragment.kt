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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexboxLayout
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleAddFavoriteEntity
import ramble.sokol.hibyeapp.databinding.FragmentCurrentEventBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view_model.ScheduleViewModel
import ramble.sokol.hibyeapp.view_model.ScheduleViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CurrentEventFragment : Fragment() {

    private var binding: FragmentCurrentEventBinding? = null
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var tokenManager: TokenManager

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

        scheduleViewModel = ViewModelProvider(
            this,
            ScheduleViewModelFactory((requireActivity().application as MyApplication).scheduleRepository)
        ).get(ScheduleViewModel::class.java)

        tokenManager = (requireActivity().application as MyApplication).tokenManager

        val scheduleId = arguments?.getLong("scheduleId", -1) ?: -1
        val parentId = arguments?.getLong("parentId", -1) ?: -1
        val title = arguments?.getString("title", "") ?: ""
        val time = arguments?.getString("timeStart", "") ?: ""
        val timeEnd = arguments?.getString("timeEnd", "") ?: ""
        val description = arguments?.getString("description", "") ?: ""
        val tagsList = arguments?.getStringArrayList("tags") ?: ""
        val favoriteScheduleIds = arguments?.getLongArray("favoriteScheduleIds")?.toList() ?: emptyList<Long>()
        val formattedTime = formatTime(time)
        val (date, timeFinal) = formattedTime!!

        Log.d("MyLog", favoriteScheduleIds.toString())

        binding!!.name.text = title
        binding!!.date.text = date
        binding!!.time.text = timeFinal
        binding!!.description.text = description

        if (favoriteScheduleIds.contains(scheduleId)) {
            binding!!.buttonAddFavorite.visibility = View.GONE
            binding!!.textFavorite.visibility = View.VISIBLE
        } else {
            binding!!.buttonAddFavorite.visibility = View.VISIBLE
            binding!!.textFavorite.visibility = View.GONE
        }

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

        binding!!.buttonAddFavorite.setOnClickListener {

            binding!!.buttonAddFavorite.visibility = View.INVISIBLE
            binding!!.progressLogin.visibility = View.VISIBLE

            if (scheduleId != -1L) {
                scheduleViewModel.addFavorite(
                    ScheduleAddFavoriteEntity(
                        userId = tokenManager.getUserIdTelegram(),
                        scheduleId = scheduleId
                    )
                )
            }
        }

        scheduleViewModel.addFavorite.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                binding!!.buttonAddFavorite.visibility = View.INVISIBLE
                binding!!.progressLogin.visibility = View.INVISIBLE
                binding!!.textFavorite.visibility = View.VISIBLE
            } else if (result.isFailure) {
                binding!!.textFavorite.visibility = View.INVISIBLE
                binding!!.buttonAddFavorite.visibility = View.VISIBLE
                binding!!.progressLogin.visibility = View.INVISIBLE
                // Обработка ошибки
                Log.e("CurrentEventFragment", "Error adding to favorite: ${result.exceptionOrNull()?.message}")
            }
        })

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

        layoutTags.removeAllViews()

        val tagColors: TypedArray = resources.obtainTypedArray(R.array.tag_colors)

        for ((index, tag) in tags.withIndex()) {
            val tagView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tag, layoutTags, false)

            val tagText = tagView.findViewById<TextView>(R.id.tag_text)
            tagText.text = tag

            val cardView = tagView.findViewById<androidx.cardview.widget.CardView>(R.id.card_view)
            val color = tagColors.getColor(index % tagColors.length(), Color.BLACK)
            cardView.setCardBackgroundColor(color)

            layoutTags.addView(tagView)
        }

        tagColors.recycle()
    }

}


//