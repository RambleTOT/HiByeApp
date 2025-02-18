package ramble.sokol.hibyeapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ramble.sokol.hibyeapp.databinding.FragmentQuickMeetBinding

class QuickMeetFragment : Fragment() {

    private var binding: FragmentQuickMeetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuickMeetBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }


}