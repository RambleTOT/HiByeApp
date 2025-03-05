package ramble.sokol.hibyeapp.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.databinding.FragmentNewEventBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.MeetsAdapter
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory
import ramble.sokol.hibyeapp.view_model.MeetsViewModel
import ramble.sokol.hibyeapp.view_model.MeetsViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class NewEventFragment : Fragment() {

    private var binding: FragmentNewEventBinding? = null
    private var selectedDate: Calendar? = null
    private var isEmptyName = false
    private var isEmptyDescription = false
    private var isEmptyCount = false
    private var isEmptyTime = false
    private var isEmptyCheckCount = false
    private var isEmptyCheckTime = false
    private lateinit var meetsViewModel: MeetsViewModel
    private lateinit var tokenManager: TokenManager
    private var isCountValid = false

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

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userId = tokenManager.getUserIdTelegram()!!

        meetsViewModel = ViewModelProvider(
            this,
            MeetsViewModelFactory((requireActivity().application as MyApplication).meetsRepository)
        ).get(MeetsViewModel::class.java)

        binding!!.editTextNameEvent.addTextChangedListener(nameTextWatcher)
        binding!!.editTextDescriptionEvent.addTextChangedListener(descriptionTextWatcher)
        binding!!.editTextCount.addTextChangedListener(countTextWatcher)

        binding!!.editTextNameEvent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                binding!!.editTextDescriptionEvent.requestFocus()
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
            isEmptyCheckTime = isChecked
            blockButton()
        }

        binding!!.switchCount.setOnCheckedChangeListener { _, isChecked ->
            binding!!.editTextCount.visibility = if (isChecked) View.VISIBLE else View.GONE
            isEmptyCheckCount = isChecked
            blockButton()
        }

        binding!!.buttonChooseDate.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
            showDateTimePicker()
        }

        binding!!.textButtonCreate.setOnClickListener {
            binding!!.textButtonCreate.startAnimation(scaleDown)
            binding!!.textButtonCreate.startAnimation(scaleUp)
            binding!!.textButtonCreate.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_hint_color)
            )
            binding!!.textButtonCreate.isEnabled = false
            val name = binding!!.editTextNameEvent.text.toString()
            val description = binding!!.editTextDescriptionEvent.text.toString()
            val count = binding!!.editTextCount.text.toString()
            Log.d("MyLog", "${binding!!.textDate.toString()} ${binding!!.textTime.toString()}")
            val time = formatDateTime(binding!!.textDate.text.toString(), binding!!.textTime.text.toString())
            meetsViewModel.createGroupMeeting(
                eventId!!,
                MeetingResponse(
                    eventId = eventId,
                    meetingId = null,
                    organisatorId = userId,
                    name = name,
                    description = description,
                    capacity = count.toInt(),
                    timeStart = time,
                    meetingType = "CUSTOM_MEETING",
                    meetingStatus = "AWAITING_RESPONSE",
                    mark = 0,
                    meetingNote = "string",
                    userIds = listOf(userId),
                    card = 0,
                    telegramChatLink = "string",
                    chatId = null,
                    isOfficeActivity = false,
                    specialMeetingInfo = null
                )
            )
        }

        meetsViewModel.createGroupMeeting.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val res = result.getOrNull()
                Log.d("MyLog", "group $res")
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val bottomNavBarFragment = BottomNavBarFragment(NetworkingFragment())
                transaction.replace(R.id.layout_fragment, bottomNavBarFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            } else if (result.isFailure) {
                binding!!.textButtonCreate.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.main_color)
                )
                binding!!.textButtonCreate.isEnabled = true
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading meets: ${exception?.message}")
            }
        })

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
            blockButton()
            Log.d("MyLog","Дата: $formattedDate, Время: $formattedTime")
        } else {
            Log.d("MyLog", "Error")
            isEmptyTime = false
            blockButton()
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
            val text = p0.toString().trim()
            isEmptyCount = text.isNotEmpty()
            isCountValid = text.toIntOrNull() != null //
            if (isCountValid == true){
                if (text.toInt() < 1){
                    isCountValid = false
                }
            }
            blockButton()
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

    @SuppressLint("ResourceAsColor")
    private fun blockButton(){
        if (isEmptyName && isEmptyDescription && isEmptyCount && isEmptyTime && isEmptyCheckCount && isEmptyCheckTime && isCountValid){
            binding!!.textButtonCreate.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.main_color)
                    )
            binding!!.textButtonCreate.isEnabled = true
        }else if (!isEmptyName || !isEmptyDescription || !isEmptyCount || !isEmptyTime || !isEmptyCheckTime || !isEmptyCheckCount || !isCountValid){
            binding!!.textButtonCreate.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_hint_color)
            )
            binding!!.textButtonCreate.isEnabled = false
        }
    }

    fun formatDateTime(dateString: String, timeString: String): String {
        // Разбираем строку с датой
        val dateParts = dateString.split(" ")
        val day = dateParts[0].toInt() // День
        val month = when (dateParts[1]) { // Месяц
            "января" -> 1
            "февраля" -> 2
            "марта" -> 3
            "апреля" -> 4
            "мая" -> 5
            "июня" -> 6
            "июля" -> 7
            "августа" -> 8
            "сентября" -> 9
            "октября" -> 10
            "ноября" -> 11
            "декабря" -> 12
            else -> throw IllegalArgumentException("Неверный формат месяца")
        }
        val year = dateParts[2].replace("г.", "").trim().toInt() // Год

        // Разбираем строку с временем
        val timeParts = timeString.split(":")
        val hour = timeParts[0].toInt() // Часы
        val minute = timeParts[1].toInt() // Минуты

        // Создаем объект LocalDateTime
        val localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.of(year, month, day, hour, minute)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Форматируем результат в нужный формат
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return localDateTime.format(outputFormatter)
    }
}