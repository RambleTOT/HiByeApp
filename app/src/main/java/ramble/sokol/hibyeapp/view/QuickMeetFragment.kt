package ramble.sokol.hibyeapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentQuickMeetBinding

class QuickMeetFragment(
    val lastFragment: Fragment
) : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        val userId = arguments?.getLong("userId", -1) ?: -1
        val userName = arguments?.getString("userName", "") ?: ""
        val userInfo = arguments?.getString("userInfo", "") ?: ""
        val photoLink = arguments?.getString("photoLink", "") ?: ""

        binding!!.name.text = userName
        binding!!.descriptionMeet.text = userInfo

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding!!.textButtonBack.setOnClickListener {
            binding!!.textButtonBack.startAnimation(scaleDown)
            binding!!.textButtonBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_fragment, BottomNavBarFragment(lastFragment))
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

    }

}