package ramble.sokol.hibyeapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.databinding.FragmentLoginBinding
import ramble.sokol.hibyeapp.databinding.FragmentRegistrationBinding
import ramble.sokol.hibyeapp.view_model.AuthViewModel
import ramble.sokol.hibyeapp.view_model.AuthViewModelFactory

class RegistrationFragment : Fragment() {

    private var binding: FragmentRegistrationBinding? = null
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        val authRepository = (requireActivity().application as MyApplication).authRepository
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository)).get(AuthViewModel::class.java)
        binding!!.editTextPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding!!.editTextPhone.text.isNullOrEmpty()) {
                    binding!!.editTextPhone.setText("+7")
                    binding!!.editTextPhone.setSelection(2)
                }
            } else {
                if (binding!!.editTextPhone.text.toString() == "+7") {
                    binding!!.editTextPhone.text?.clear()
                }
            }
        }

        binding!!.editTextPhone.addTextChangedListener(phoneTextWatcher)
        binding!!.editTextPassword.addTextChangedListener(passwordTextWatcher)
        binding!!.editTextPasswordRepeat.addTextChangedListener(passwordRepeatTextWatcher)

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding!!.textButtonLogin.setOnClickListener {
            binding!!.textButtonLogin.startAnimation(scaleDown)
            binding!!.textButtonLogin.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val loginFragment = LoginFragment()
            transaction.replace(R.id.layout_fragment, loginFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

        binding!!.editTextPassword.setOnClickListener {
            binding!!.textErrorPhone.visibility = View.GONE
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.textErrorPassword.visibility = View.GONE
            binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
        }

        binding!!.editTextPasswordRepeat.setOnClickListener {
            binding!!.textErrorPhone.visibility = View.GONE
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.textErrorPassword.visibility = View.GONE
            binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
        }

        binding!!.editTextPhone.setOnClickListener {
            binding!!.textErrorPhone.visibility = View.GONE
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.textErrorPassword.visibility = View.GONE
            binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
        }

        binding!!.buttonRegistration.setOnClickListener {
            val phone = binding!!.editTextPhone.text.toString()
            val password = binding!!.editTextPassword.text.toString()
            val passwordRepeat = binding!!.editTextPasswordRepeat.text.toString()
            if (phone.length <= 2){
                binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background_error)
            }
            if (password.isEmpty()){
                binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background_error)
            }
            if (passwordRepeat.isEmpty()){
                binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background_error)
            }
            if (!isPhoneValid(phone)){
                binding!!.textErrorPhone.visibility = View.VISIBLE
            }
            if (password.length < 8){
                binding!!.textErrorPassword.setText(R.string.text_error_len_password)
                binding!!.textErrorPassword.visibility = View.VISIBLE
            }
            if (password.isNotEmpty() && phone.length > 2 && passwordRepeat.isNotEmpty() && isPhoneValid(phone) && password.length >= 8) {
                if (password != passwordRepeat){
                    binding!!.textErrorLogin.setText(R.string.text_error_password)
                    binding!!.textErrorLogin.visibility = View.VISIBLE
                }else{
                    binding!!.buttonRegistration.visibility = View.INVISIBLE
                    binding!!.progressLogin.visibility = View.VISIBLE
                    binding!!.textErrorLogin.visibility = View.GONE
                }
            }


//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            val codeEventFragment = CodeEventFragment()
//            transaction.replace(R.id.layout_fragment, codeEventFragment)
//            transaction.disallowAddToBackStack()
//            transaction.commit()
        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.textErrorPhone.visibility = View.GONE
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.textErrorPassword.visibility = View.GONE
            binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    private val passwordRepeatTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.textErrorPhone.visibility = View.GONE
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.textErrorPassword.visibility = View.GONE
            binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    private val phoneTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding!!.textErrorPhone.visibility = View.GONE
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.textErrorPassword.visibility = View.GONE
            binding!!.editTextPhone.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPassword.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
            binding!!.editTextPasswordRepeat.background = ContextCompat.getDrawable(requireActivity(), R.drawable.edit_text_background)
        }

        override fun afterTextChanged(editable: Editable?) {
            // Убедимся, что префикс +7 не удаляется
            if (editable?.length ?: 0 < 2) {
                binding!!.editTextPhone.setText("+7")
                binding!!.editTextPhone.setSelection(2) // Устанавливаем курсор после +7
                return
            }

            Log.d("MyLog", binding!!.editTextPhone.text.toString())

            // Удаляем все символы, кроме цифр
            val digitsOnly = editable.toString().replace(Regex("[^\\d]"), "")
            val formatted = formatPhoneNumber(digitsOnly)

            // Устанавливаем форматированный текст
            if (editable.toString() != formatted) {
                binding!!.editTextPhone.setText(formatted)
                binding!!.editTextPhone.setSelection(formatted.length) // Устанавливаем курсор в конец
            }
        }
    }

    private fun formatPhoneNumber(digits: String): String {
        val sb = StringBuilder("+7")

        if (digits.length > 1) {
            val number = digits.substring(1) // Игнорируем первую цифру (префикс +7)
            if (number.length > 0) {
                sb.append(" (")
                if (number.length > 3) {
                    sb.append(number.substring(0, 3))
                    sb.append(") ")
                    if (number.length > 6) {
                        sb.append(number.substring(3, 6))
                        sb.append(" ")
                        if (number.length > 8) {
                            sb.append(number.substring(6, 8))
                            sb.append("-")
                            if (number.length > 10) {
                                sb.append(number.substring(8, 10))
                            } else {
                                sb.append(number.substring(8))
                            }
                        } else {
                            sb.append(number.substring(6))
                        }
                    } else {
                        sb.append(number.substring(3))
                    }
                } else {
                    sb.append(number)
                }
            }
        }

        return sb.toString()
    }

    fun isPhoneValid(phone: String): Boolean {
        val phoneRegex = Regex("^\\+7 \\(\\d{3}\\) \\d{3} \\d{2}-\\d{2}\$")
        return phoneRegex.matches(phone)
    }


}