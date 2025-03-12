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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentCreateProfileBinding
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.ProfileAndCodeManager
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class CreateProfileFragment : Fragment() {

    private var binding: FragmentCreateProfileBinding? = null
    private lateinit var nameAndPhotoManager: NameAndPhotoManager
    private lateinit var profileAndCodeManager: ProfileAndCodeManager
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_CODE_PERMISSIONS = 100
    private var imageRequestBody = ""

    private lateinit var transferUtility: TransferUtility
    private lateinit var s3Client: AmazonS3Client

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initAWS()
    }

    private fun init() {

        profileAndCodeManager = ProfileAndCodeManager(requireActivity())
        nameAndPhotoManager = NameAndPhotoManager(requireActivity())

        binding!!.editTextName.addTextChangedListener(nameTextWatcher)
        binding!!.editTextName.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextName.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.edit_text_background
            )
        }

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

        binding!!.imageParticipant.setOnClickListener {
            binding!!.imageParticipant.startAnimation(scaleDown)
            binding!!.imageParticipant.startAnimation(scaleUp)
            openGallery()
        }

        binding!!.buttonSave.setOnClickListener {
            binding!!.textErrorLogin.visibility = View.GONE
            val name = binding!!.editTextName.text.toString()
            if (name.isEmpty()) {
                binding!!.editTextName.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (imageRequestBody.isNullOrEmpty()){
                binding!!.textErrorLogin.visibility = View.VISIBLE
            }
            if (name.isNotEmpty() && imageRequestBody.isNotEmpty()) {
                binding!!.buttonSave.visibility = View.INVISIBLE
                binding!!.progressLogin.visibility = View.VISIBLE
                binding!!.textErrorLogin.visibility = View.GONE
                binding!!.imageParticipant.isEnabled = false
                nameAndPhotoManager.savePhoto(imageRequestBody)
                nameAndPhotoManager.saveName(name)
                profileAndCodeManager.saveProfile(true)
                Log.d("MyLog", nameAndPhotoManager.getPhoto().toString())
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val codeEventFragment = CodeEventFragment()
                transaction.replace(R.id.layout_fragment, codeEventFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }
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

    private var nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.textErrorLogin.visibility = View.GONE
            binding!!.editTextName.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.edit_text_background
            )
        }

        override fun afterTextChanged(p0: Editable?) {

        }
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
                uploadImageToS3(imageUri)
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