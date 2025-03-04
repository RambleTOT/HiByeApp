package ramble.sokol.hibyeapp.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.MyApplication
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory

class AddEventsDialog(
    private val onDismiss: () -> Unit
) : DialogFragment() {

    private lateinit var code1: EditText
    private lateinit var code2: EditText
    private lateinit var code3: EditText
    private lateinit var code4: EditText
    private lateinit var code5: EditText
    private lateinit var code6: EditText
    private lateinit var progressLogin: ProgressBar
    private lateinit var errorLogin: TextView
    private lateinit var buttonLogin: MaterialButton
    private lateinit var buttonBack: TextView
    private lateinit var tokenManager: TokenManager
    private lateinit var eventsViewModel: EventsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_code, null)
        code1 = view.findViewById<EditText>(R.id.code1dialog)
        code2 = view.findViewById<EditText>(R.id.code2dialog)
        code3 = view.findViewById<EditText>(R.id.code3dialog)
        code4 = view.findViewById<EditText>(R.id.code4dialog)
        code5 = view.findViewById<EditText>(R.id.code5dialog)
        code6 = view.findViewById<EditText>(R.id.code6dialog)
        setupCodeInputListeners()
        val eventsRepository = (requireActivity().application as MyApplication).eventsRepository
        eventsViewModel = ViewModelProvider(this, EventsViewModelFactory(eventsRepository)).get(EventsViewModel::class.java)
        buttonLogin = view.findViewById<MaterialButton>(R.id.button_add_code_new_event)
        buttonBack = view.findViewById<TextView>(R.id.text_button_back_code_dialog)
        errorLogin = view.findViewById<TextView>(R.id.text_error_login)
        progressLogin = view.findViewById<ProgressBar>(R.id.progress_add_code_dialog)
        init()

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun init(){
        val eventsRepository = (requireActivity().application as MyApplication).eventsRepository
        eventsViewModel = ViewModelProvider(this, EventsViewModelFactory(eventsRepository)).get(EventsViewModel::class.java)
        tokenManager = TokenManager(requireActivity())
        code1.setOnClickListener {
            hideError()
        }
        code2.setOnClickListener {
            hideError()
        }
        code3.setOnClickListener {
            hideError()
        }
        code4.setOnClickListener {
            hideError()
        }
        code5.setOnClickListener {
            hideError()
        }
        code6.setOnClickListener {
            hideError()
        }

        code1.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        code2.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        code3.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        code4.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        code5.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        code6.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        buttonBack.setOnClickListener {
            val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
            val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
            buttonBack.startAnimation(scaleDown)
            buttonBack.startAnimation(scaleUp)
            dismiss()
        }

        buttonLogin.setOnClickListener {

            val codeString1 = code1.text.toString()
            val codeString2 = code2.text.toString()
            val codeString3 = code3.text.toString()
            val codeString4 = code4.text.toString()
            val codeString5 = code5.text.toString()
            val codeString6 = code6.text.toString()

            if (codeString1.isEmpty()){
                code1.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString2.isEmpty()){
                code2.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString3.isEmpty()){
                code3.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString4.isEmpty()){
                code4.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString5.isEmpty()){
                code5.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString6.isEmpty()){
                code6.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (codeString1.isNotEmpty() && codeString2.isNotEmpty() && codeString3.isNotEmpty()
                && codeString4.isNotEmpty() && codeString5.isNotEmpty() && codeString6.isNotEmpty()){

                buttonLogin.visibility = View.INVISIBLE
                progressLogin.visibility = View.VISIBLE
                errorLogin.visibility = View.GONE
                val userId = tokenManager.getUserIdTelegram()
                val telegramId = tokenManager.getTelegramId()
                val pin = "$codeString1$codeString2$codeString3$codeString4$codeString5$codeString6"
                eventsViewModel.joinByPin(pin, telegramId!!, userId!!)

            }
        }

        eventsViewModel.joinByPinResult.observe(this, Observer { result ->

            Log.d("MyLog", result.toString())

            if (result.isSuccess) {
                Toast.makeText(
                    requireActivity(),
                    "Вы успешно вошли!",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
                onDismiss()

            }else if (result.isFailure){
                buttonLogin.visibility = View.VISIBLE
                progressLogin.visibility = View.INVISIBLE
                errorLogin.visibility = View.VISIBLE
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
            view = View(requireActivity())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideError(){
        errorLogin.visibility = View.GONE
        code1.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        code2.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        code3.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        code4.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        code5.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
        code6.background = ContextCompat.getDrawable(requireActivity(),
            R.drawable.edit_text_background
        )
    }


}