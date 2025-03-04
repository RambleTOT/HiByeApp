package ramble.sokol.hibyeapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
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
    private var isPhotoImage: Boolean = false
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_CODE_PERMISSIONS = 100

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
        val urlImage = nameAndPhotoManager.getPhoto()
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)
        isPhoto = nameUser != null
        isPhotoImage = urlImage != null

        if (isPhoto) {
            binding!!.editTextName.setText(nameUser)
            binding!!.editTextName.isEnabled = false
        }else{
            binding!!.editTextName.isEnabled = true
        }

        if (isPhotoImage) {
            loadImageWithGlide(urlImage!!)
            binding!!.imageParticipant.isEnabled = false
        }else{
            binding!!.imageParticipant.isEnabled = true
        }

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

        binding!!.imageParticipant.setOnClickListener {
            binding!!.imageParticipant.startAnimation(scaleDown)
            binding!!.imageParticipant.startAnimation(scaleUp)
            openGallery()
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

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        } else {
            checkPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {

                loadImageWithGlide(imageUri)

                //uploadImageToS3(imageUri)
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * requireContext().resources.displayMetrics.density).toInt()
    }

    private fun loadImageWithGlide(imageUri: Uri) {
        val sizeInPx = dpToPx(100)

        Glide.with(requireContext())
            .load(imageUri)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .override(sizeInPx, sizeInPx)
            .into(binding!!.imageParticipant)
    }

    private fun loadImageWithGlide(imageUrl: String) {
        val sizeInPx = dpToPx(100)

        Glide.with(requireContext())
            .load(imageUrl)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .override(sizeInPx, sizeInPx)
            .into(binding!!.imageParticipant)
    }

}