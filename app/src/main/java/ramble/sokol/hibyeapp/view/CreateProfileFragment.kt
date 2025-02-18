package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentCreateProfileBinding
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.ProfileAndCodeManager

class CreateProfileFragment : Fragment() {

    private var binding: FragmentCreateProfileBinding? = null
    private lateinit var nameAndPhotoManager: NameAndPhotoManager
    private lateinit var profileAndCodeManager: ProfileAndCodeManager

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
    }

    private fun init(){

        profileAndCodeManager = ProfileAndCodeManager(requireActivity())
        nameAndPhotoManager = NameAndPhotoManager(requireActivity())

        binding!!.editTextName.addTextChangedListener(nameTextWatcher)
        binding!!.editTextName.setOnClickListener {
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_background
            )
        }

        binding!!.buttonSave.setOnClickListener {
            val name = binding!!.editTextName.text.toString()
            if (name.isEmpty()){
                binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                    R.drawable.edit_text_background_error
                )
            }
            if (name.isNotEmpty()) {
                binding!!.buttonSave.visibility = View.INVISIBLE
                binding!!.progressLogin.visibility = View.VISIBLE
                nameAndPhotoManager.saveName(name)
                profileAndCodeManager.saveProfile(true)
                Log.d("MyLog", nameAndPhotoManager.getName().toString())
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val codeEventFragment = CodeEventFragment()
                transaction.replace(R.id.layout_fragment, codeEventFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }
    }

    private var nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.editTextName.background = ContextCompat.getDrawable(requireActivity(),
                R.drawable.edit_text_background
            )
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

}