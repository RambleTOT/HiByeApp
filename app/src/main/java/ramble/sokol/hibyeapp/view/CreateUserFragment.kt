package ramble.sokol.hibyeapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.databinding.FragmentCreateUserBinding
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory
import java.io.File
import java.io.IOException
import java.io.InputStream

class CreateUserFragment : Fragment() {

    private var binding: FragmentCreateUserBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var nameAndPhotoManager: NameAndPhotoManager
    private var isPhoto: Boolean = false
    private var isPhotoImage: Boolean = false
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_CODE_PERMISSIONS = 100
    private var imageRequestBody:String? = ""
    private lateinit var transferUtility: TransferUtility
    private lateinit var s3Client: AmazonS3Client

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
        imageRequestBody = nameAndPhotoManager.getPhoto()

        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)
        isPhoto = nameUser != null
        isPhotoImage = imageRequestBody != null

        if (isPhoto) {
            binding!!.editTextName.setText(nameUser)
            binding!!.editTextName.isEnabled = false
        }else{
            binding!!.editTextName.isEnabled = true
        }

        if (imageRequestBody != null){
            binding!!.imageParticipant.isEnabled = false
            Glide.with(binding!!.imageParticipant.context)
                .load(imageRequestBody)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.imageParticipant.setImageResource(R.drawable.icon_profile)
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }
                })
                .into(binding!!.imageParticipant)
        } else {
            binding!!.imageParticipant.setImageResource(R.drawable.icon_profile)
            binding!!.imageParticipant.isEnabled = true
        }

        if (isPhotoImage) {
            loadImageWithGlide(imageRequestBody!!)
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
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        binding!!.editTextRequest.addTextChangedListener(requestTextWatcher)
        binding!!.editTextRequest.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
        }

        binding!!.editTextAboutMe.addTextChangedListener(aboutMeTextWatcher)
        binding!!.editTextAboutMe.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
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
            if (imageRequestBody.isNullOrEmpty()){
                binding!!.textErrorLogin.visibility = View.VISIBLE
            }
            if (name.isNotEmpty() && request.isNotEmpty() && aboutMe.isNotEmpty() && !(imageRequestBody.isNullOrEmpty())) {
                binding!!.buttonSave.visibility = View.INVISIBLE
                binding!!.textErrorLogin.visibility = View.GONE
                binding!!.progressLogin.visibility = View.VISIBLE
                Log.d("MyLog", imageRequestBody.toString())
                eventViewModel.createUser(
                    eventId = eventId!!,
                    userId = userId!!,
                    CreateUserEntity(
                        userId = userId,
                        eventId = eventId,
                        userInfo = aboutMe,
                        userName = name,
                        request = request,
                        photoLink = imageRequestBody
                    )
                )
            }
        }

        eventViewModel.createUser.observe(viewLifecycleOwner, Observer { result ->
            Log.d("MyLog", "Res^ $result")
            if (result.isSuccess) {
                Log.d("MyLog", "$result")
                val res = result.getOrNull()
                Log.d("MyLog", res!!.photoLink.toString())
                Log.d("MyLog", imageRequestBody.toString())
                binding!!.progressLogin.visibility = View.INVISIBLE
                nameAndPhotoManager.saveName(res!!.userName.toString())
                nameAndPhotoManager.saveAbout(res.userInfo.toString())
                nameAndPhotoManager.saveRequest(res.request.toString())
                nameAndPhotoManager.savePhoto(imageRequestBody.toString())
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
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
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
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
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
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextAboutMe.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextRequest.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_event_background
            )
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
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

    private fun initAWS() {
        val credentials = BasicAWSCredentials("8TC3UFX2FVA7V60TCY8O", "v3OhhkJldIN6b2xc0uOclxcHa78u42jWPYIxVDnx")


        val clientConfiguration = ClientConfiguration()
        clientConfiguration.protocol = Protocol.HTTPS


        val endpoint = "https://s3.timeweb.cloud"

        s3Client = AmazonS3Client(credentials, clientConfiguration)
        s3Client.setEndpoint(endpoint)

        // Инициализация TransferUtility
        transferUtility = TransferUtility.builder()
            .s3Client(s3Client)
            .context(requireContext())
            .build()
    }

    private fun uploadImageToS3(imageUri: Uri) {
        // Получаем InputStream из Uri
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
        if (inputStream == null) {
            Toast.makeText(requireContext(), "Failed to open image stream", Toast.LENGTH_SHORT).show()
            return
        }

        // Создаем временный файл
        val file = createTempFileFromInputStream(inputStream)
        if (file == null) {
            Toast.makeText(requireContext(), "Failed to create temp file", Toast.LENGTH_SHORT).show()
            return
        }

        val key = "images/${file.name}"

        val uploadObserver = transferUtility.upload(
            "94cd268c-92ba957c-8780-46b1-ad91-5ce39903e72e", // Bucket name
            key,
            file
        )

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    val imageUrl = s3Client.getUrl("94cd268c-92ba957c-8780-46b1-ad91-5ce39903e72e", key).toString()
                    imageRequestBody = imageUrl
                    Log.d("MyLog", "Image URL: $imageUrl")
                    loadImageWithGlide(imageUrl)
                } else if (state == TransferState.FAILED) {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDone = (bytesCurrent.toFloat() / bytesTotal.toFloat() * 100).toInt()
                Log.d("MyLog", "Progress: $percentDone%")
            }

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
                Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createTempFileFromInputStream(inputStream: InputStream): File? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            inputStream.close()
        }
    }

}