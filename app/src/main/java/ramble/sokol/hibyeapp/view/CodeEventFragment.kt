package ramble.sokol.hibyeapp.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import ramble.sokol.hibyeapp.databinding.FragmentCodeEventBinding


class CodeEventFragment : Fragment() {

    private var binding: FragmentCodeEventBinding? = null
    private lateinit var code1: EditText
    private lateinit var code2: EditText
    private lateinit var code3: EditText
    private lateinit var code4: EditText
    private lateinit var code5: EditText
    private lateinit var code6: EditText

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
        code1 = binding!!.code1
        code2 = binding!!.code2
        code3 = binding!!.code3
        code4 = binding!!.code4
        code5 = binding!!.code5
        code6 = binding!!.code6
        setupCodeInputListeners()
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

}