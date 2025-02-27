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
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.databinding.FragmentCreateProfileBinding
import ramble.sokol.hibyeapp.databinding.FragmentCreateUserBinding
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory

class CreateUserFragment : Fragment() {

    private var binding: FragmentCreateUserBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var nameAndPhotoManager: NameAndPhotoManager
    private var isPhoto: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateUserBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        nameAndPhotoManager = NameAndPhotoManager(requireActivity())
        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userId = tokenManager.getUserIdTelegram()
        val nameUser = nameAndPhotoManager.getName()
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)
        isPhoto = nameUser != null

        if (isPhoto) {
            binding!!.editTextName.setText(nameUser)
            binding!!.editTextName.isEnabled = false
        }else{
            binding!!.editTextName.isEnabled = true
        }

        binding!!.editTextName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                binding!!.editTextAboutMe.requestFocus() // Переход к editText2
                true
            } else {
                false
            }
        }

        binding!!.editTextAboutMe.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                binding!!.editTextRequest.requestFocus() // Переход к editText2
                true
            } else {
                false
            }
        }

        binding!!.editTextRequest.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding!!.editTextName.addTextChangedListener(nameTextWatcher)
        binding!!.editTextName.setOnClickListener {
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        binding!!.editTextRequest.addTextChangedListener(requestTextWatcher)
        binding!!.editTextRequest.setOnClickListener {
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        binding!!.editTextAboutMe.addTextChangedListener(aboutMeTextWatcher)
        binding!!.editTextAboutMe.setOnClickListener {
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        binding!!.buttonSave.setOnClickListener {
            val name = binding!!.editTextName.text.toString()
            val request = binding!!.editTextRequest.text.toString()
            val aboutMe = binding!!.editTextAboutMe.text.toString()
            if (name.isEmpty()){
                binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (request.isEmpty()){
                binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (aboutMe.isEmpty()){
                binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (name.isNotEmpty() && request.isNotEmpty() && aboutMe.isNotEmpty()) {
                binding!!.buttonSave.visibility = View.INVISIBLE
                binding!!.progressLogin.visibility = View.VISIBLE
                eventViewModel.createUser(
                    eventId = eventId!!,
                    userId = userId!!,
                    CreateUserEntity(
                        userId = userId,
                        eventId = eventId,
                        userInfo = aboutMe,
                        userName = name,
                        request = request,
                        photoLink = "string"
                    )
                )
            }
        }

        eventViewModel.createUser.observe(viewLifecycleOwner, Observer { result ->
            Log.d("MyLog", "Res^ $result")
            if (result.isSuccess) {
                Log.d("MyLog", "$result")
                val res = result.getOrNull()
                binding!!.progressLogin.visibility = View.INVISIBLE
                nameAndPhotoManager.saveName(res!!.userName.toString())
                nameAndPhotoManager.saveAbout(res.userInfo.toString())
                nameAndPhotoManager.saveRequest(res.request.toString())
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val bottomNavBarFragment = BottomNavBarFragment(NetworkingFragment())
                transaction.replace(R.id.layout_fragment, bottomNavBarFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            } else if (result.isFailure) {
                Log.d("MyLog", "$result")
                binding!!.buttonSave.visibility = View.VISIBLE
                binding!!.progressLogin.visibility = View.INVISIBLE
                val exception = result.exceptionOrNull()
                //Toast.makeText(context, "Login failed: ${exception!!.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private var nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    private var requestTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    private var aboutMeTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        override fun afterTextChanged(p0: Editable?) {

        }
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