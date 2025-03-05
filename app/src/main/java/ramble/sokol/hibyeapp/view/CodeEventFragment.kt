package ramble.sokol.hibyeapp.view

import android.content.Context
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
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentCodeEventBinding
import ramble.sokol.hibyeapp.managers.EmptyEventsManager
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.ProfileAndCodeManager
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory


class CodeEventFragment : Fragment() {

    private var binding: FragmentCodeEventBinding? = null
    private lateinit var code1: EditText
    private lateinit var code2: EditText
    private lateinit var code3: EditText
    private lateinit var code4: EditText
    private lateinit var code5: EditText
    private lateinit var code6: EditText
    private lateinit var nameAndPhotoManager: NameAndPhotoManager
    private lateinit var tokenManager: TokenManager
    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var profileAndCodeManager: ProfileAndCodeManager
    private lateinit var emptyEventsManager: EmptyEventsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCodeEventBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        val eventsRepository = (requireActivity().application as MyApplication).eventsRepository
        eventsViewModel = ViewModelProvider(this, EventsViewModelFactory(eventsRepository)).get(EventsViewModel::class.java)
        nameAndPhotoManager = NameAndPhotoManager(requireActivity())
        tokenManager = TokenManager(requireActivity())
        emptyEventsManager = EmptyEventsManager(requireActivity())
        profileAndCodeManager = ProfileAndCodeManager(requireActivity())
        val userName = nameAndPhotoManager.getName()
        binding!!.textHelloCode.text = "Привет, ${userName}!"

        binding!!.textButtonLogin.setOnClickListener {

            val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
            val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
            binding!!.textButtonLogin.startAnimation(scaleDown)
            binding!!.textButtonLogin.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val loginFragment = LoginFragment()
            transaction.replace(R.id.layout_fragment, loginFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

        binding!!.code1.setOnClickListener {
            hideError()
        }
        binding!!.code2.setOnClickListener {
            hideError()
        }
        binding!!.code3.setOnClickListener {
            hideError()
        }
        binding!!.code4.setOnClickListener {
            hideError()
        }
        binding!!.code5.setOnClickListener {
            hideError()
        }
        binding!!.code6.setOnClickListener {
            hideError()
        }

        binding!!.code1.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.code2.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.code3.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.code4.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.code5.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.code6.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        code1 = binding!!.code1
        code2 = binding!!.code2
        code3 = binding!!.code3
        code4 = binding!!.code4
        code5 = binding!!.code5
        code6 = binding!!.code6
        setupCodeInputListeners()

        binding!!.buttonLoginEvent.setOnClickListener {

            val codeString1 = binding!!.code1.text.toString()
            val codeString2 = binding!!.code2.text.toString()
            val codeString3 = binding!!.code3.text.toString()
            val codeString4 = binding!!.code4.text.toString()
            val codeString5 = binding!!.code5.text.toString()
            val codeString6 = binding!!.code6.text.toString()

            if (codeString1.isEmpty()){
                binding!!.code1.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString2.isEmpty()){
                binding!!.code2.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString3.isEmpty()){
                binding!!.code3.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString4.isEmpty()){
                binding!!.code4.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString5.isEmpty()){
                binding!!.code5.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString6.isEmpty()){
                binding!!.code6.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString1.isNotEmpty() && codeString2.isNotEmpty() && codeString3.isNotEmpty()
                && codeString4.isNotEmpty() && codeString5.isNotEmpty() && codeString6.isNotEmpty()){

                binding!!.buttonLoginEvent.visibility = View.INVISIBLE
                binding!!.progressLogin.visibility = View.VISIBLE
                binding!!.textErrorLogin.visibility = View.GONE
                val userId = tokenManager.getUserIdTelegram()
                val telegramId = tokenManager.getTelegramId()
                val pin = "$codeString1$codeString2$codeString3$codeString4$codeString5$codeString6"
                eventsViewModel.joinByPin(pin, telegramId!!, userId!!)

            }
        }

        eventsViewModel.joinByPinResult.observe(viewLifecycleOwner, Observer { result ->

            Log.d("MyLog", result.toString())

            if (result.isSuccess) {

                profileAndCodeManager.saveProfile(false)
                profileAndCodeManager.saveRegistr(false)
                profileAndCodeManager.saveCode(false)
                emptyEventsManager.saveEmptyEvent(true)
                emptyEventsManager.saveLogin(true)
                emptyEventsManager.saveIsEvent(true)
                Toast.makeText(
                    context,
                    "Вы успешно вошли!",
                    Toast.LENGTH_SHORT
                ).show()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val bottomNavBarFragment = BottomNavBarFragment(NetworkingFragment())
                transaction.replace(R.id.layout_fragment, bottomNavBarFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()

            }else if (result.isFailure){
                binding!!.buttonLoginEvent.visibility = View.VISIBLE
                binding!!.progressLogin.visibility = View.INVISIBLE
                val exception = result.exceptionOrNull()
                //Toast.makeText(context, "Login failed: ${exception!!.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setupCodeInputListeners() {
        code1.addTextChangedListener(CodeTextWatcher(code1, code2))
        code2.addTextChangedListener(CodeTextWatcher(code2, code3))
        code3.addTextChangedListener(CodeTextWatcher(code3, code4))
        code4.addTextChangedListener(CodeTextWatcher(code4, code5))
        code5.addTextChangedListener(CodeTextWatcher(code5, code6))
        code6.addTextChangedListener(CodeTextWatcher(code6, null))
    }

    private inner class CodeTextWatcher(
        private val currentEditText: EditText,
        private val nextEditText: EditText?
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            hideError()
            if (s?.length == 1) {
                nextEditText?.requestFocus() // Переход к следующему блоку
            }else if (s.isNullOrEmpty() && currentEditText != code1) {
                // Переход к предыдущему блоку при удалении символа
                when (currentEditText) {
                    code2 -> code1.requestFocus()
                    code3 -> code2.requestFocus()
                    code4 -> code3.requestFocus()
                    code5 -> code4.requestFocus()
                    code6 -> code5.requestFocus()
                }
            }

            // Скрытие клавиатуры при заполнении последнего блока
            if (currentEditText == code6 && s?.length == 1) {
                hideKeyboard()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(requireActivity()) // Создаем dummy View, если фокус не установлен
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideError(){
        binding!!.textErrorLogin.visibility = View.GONE
        binding!!.code1.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        binding!!.code2.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        binding!!.code3.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        binding!!.code4.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        binding!!.code5.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        binding!!.code6.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
    }

}