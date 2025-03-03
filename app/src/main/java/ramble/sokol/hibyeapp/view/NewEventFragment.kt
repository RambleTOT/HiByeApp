package ramble.sokol.hibyeapp.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentNewEventBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEventFragment : Fragment() {

    private var binding: FragmentNewEventBinding? = null
    private var selectedDate: Calendar? = null
    private var isEmptyName = false
    private var isEmptyDescription = false
    private var isEmptyCount = false
    private var isEmptyTime = false

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

        binding!!.editTextNameEvent.addTextChangedListener(nameTextWatcher)
        binding!!.editTextDescriptionEvent.addTextChangedListener(descriptionTextWatcher)
        binding!!.editTextCount.addTextChangedListener(countTextWatcher)

        binding!!.editTextNameEvent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                binding!!.editTextDescriptionEvent.requestFocus() // Переход к editText2
                true
            } else {
                false
            }
        }

        binding!!.editTextDescriptionEvent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.editTextCount.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.editTextNameEvent.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
        }

        binding!!.editTextDescriptionEvent.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
        }

        binding!!.editTextCount.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
        }

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
            binding!!.textErrorLogin.visibility = View.GONE
            showDateTimePicker()
        }

        binding!!.textButtonCreate.setOnClickListener {
            binding!!.textButtonCreate.startAnimation(scaleDown)
            binding!!.textButtonCreate.startAnimation(scaleUp)
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
            binding!!.layoutDate.visibility = View.VISIBLE
            binding!!.layoutTime.visibility = View.VISIBLE
            binding!!.textDate.visibility = View.VISIBLE
            binding!!.textTime.visibility = View.VISIBLE
            isEmptyTime = true
            Log.d("MyLog","Дата: $formattedDate, Время: $formattedTime")
        } else {
            Log.d("MyLog", "Error")
            isEmptyTime = false
            println("Ошибка: выберите дату и время в будущем")
            binding!!.textErrorLogin.visibility = View.VISIBLE
        }
    }

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.textErrorLogin.visibility = View.GONE

        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().trim().isEmpty()) {
                isEmptyName = false
                blockButton()
            }else {
                isEmptyName = true
                blockButton()
            }
        }
    }

    private val countTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.textErrorLogin.visibility = View.GONE

        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().trim().isEmpty()) {
                isEmptyCount = false
                blockButton()
            }else {
                isEmptyCount = true
                blockButton()
            }
        }
    }

    private val descriptionTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.textErrorLogin.visibility = View.GONE

        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().trim().isEmpty()) {
                isEmptyDescription = false
                blockButton()
            }else {
                isEmptyDescription = true
                blockButton()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(requireActivity())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun blockButton(){
        if (isEmptyName && isEmptyDescription && isEmptyCount && isEmptyTime){
            binding!!.textButtonCreate.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.main_color))
            binding!!.textButtonCreate.isEnabled = true
        }else if (!isEmptyName || !isEmptyDescription || !isEmptyCount || !isEmptyTime){
            binding!!.textButtonCreate.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.text_hint_color))
            binding!!.textButtonCreate.isEnabled = false
        }
    }


}