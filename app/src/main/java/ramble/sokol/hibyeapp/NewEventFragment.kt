package ramble.sokol.hibyeapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import ramble.sokol.hibyeapp.databinding.FragmentNetworkingBinding
import ramble.sokol.hibyeapp.databinding.FragmentNewEventBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEventFragment : Fragment() {

    private var binding: FragmentNewEventBinding? = null
    private var selectedDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewEventBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

        binding!!.textButtonBack.setOnClickListener {
            binding!!.textButtonBack.startAnimation(scaleDown)
            binding!!.textButtonBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val bottomNavBarFragment = BottomNavBarFragment(NetworkingFragment())
            transaction.replace(R.id.layout_fragment, bottomNavBarFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

        binding!!.switchDate.setOnCheckedChangeListener { _, isChecked ->
            binding!!.buttonChooseDate.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding!!.switchCount.setOnCheckedChangeListener { _, isChecked ->
            binding!!.editTextCount.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding!!.buttonChooseDate.setOnClickListener {
            showDateTimePicker()
        }


    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                showTimePicker()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000 // Запрет выбора прошлых дат
        datePicker.show()
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                selectedDate?.set(Calendar.HOUR_OF_DAY, hour)
                selectedDate?.set(Calendar.MINUTE, minute)
                validateAndFormatDateTime()
            },
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun validateAndFormatDateTime() {
        val currentDate = Calendar.getInstance()
        if (selectedDate != null && selectedDate!!.after(currentDate)) {
            val dateFormat = SimpleDateFormat("d MMMM yyyy г.", Locale("ru"))
            val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
            val formattedDate = dateFormat.format(selectedDate!!.time)
            val formattedTime = timeFormat.format(selectedDate!!.time)
            binding!!.textDate.text = formattedDate
            binding!!.textTime.text = formattedTime
            binding!!.textDate.visibility = View.VISIBLE
            binding!!.textTime.visibility = View.VISIBLE
            Log.d("MyLog","Дата: $formattedDate, Время: $formattedTime")
        } else {
            Log.d("MyLog", "Error")
            println("Ошибка: выберите дату и время в будущем")
        }
    }

}