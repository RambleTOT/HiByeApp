package ramble.sokol.hibyeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ramble.sokol.hibyeapp.databinding.FragmentCodeEventBinding
import ramble.sokol.hibyeapp.databinding.FragmentCreateProfileBinding

class CreateProfileFragment : Fragment() {

    private var binding: FragmentCreateProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

}