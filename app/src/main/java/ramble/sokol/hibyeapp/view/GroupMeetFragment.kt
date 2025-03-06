package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.meets.MeetingIdEntity
import ramble.sokol.hibyeapp.databinding.FragmentGroupMeetBinding
import ramble.sokol.hibyeapp.databinding.FragmentQuickMeetBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.ParticipantsAdapter
import ramble.sokol.hibyeapp.view_model.MeetsViewModel
import ramble.sokol.hibyeapp.view_model.MeetsViewModelFactory


class GroupMeetFragment : Fragment() {

    private var binding: FragmentGroupMeetBinding? = null
    private lateinit var meetsViewModel: MeetsViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupMeetBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userTgId = tokenManager.getUserIdTelegram()

        meetsViewModel = ViewModelProvider(
            this,
            MeetsViewModelFactory((requireActivity().application as MyApplication).meetsRepository)
        ).get(MeetsViewModel::class.java)

        val isAvailable = arguments?.getBoolean("isAvailable", false) ?: false
        val isHistory = arguments?.getBoolean("isHistory", false) ?: false
        val meetName = arguments?.getString("meetName", "") ?: ""
        val meetDescription = arguments?.getString("meetDescription", "") ?: ""
        val meetTime = arguments?.getString("meetTime", "") ?: ""
        val organisatorId = arguments?.getLong("organisatorId", -1) ?: -1
        val meetingId = arguments?.getLong("meetingId", -1) ?: -1
        val status = arguments?.getString("status", "") ?: ""
        val count = arguments?.getString("count", "") ?: ""

        binding!!.descriptionMeet.text = meetDescription
        binding!!.name.text = meetName

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding!!.textButtonBack.setOnClickListener{
            binding!!.textButtonBack.startAnimation(scaleDown)
            binding!!.textButtonBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_fragment, BottomNavBarFragment(NetworkingFragment()))
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

        if (isHistory){
            binding!!.textHistory.visibility = View.VISIBLE
            binding!!.frameAnswer.visibility = View.GONE
            binding!!.buttonFilters.visibility = View.GONE
            binding!!.linearAccpeted.visibility = View.GONE
        }else if (isAvailable){
            binding!!.textHistory.visibility = View.GONE
            binding!!.frameAnswer.visibility = View.VISIBLE
            binding!!.buttonFilters.visibility = View.VISIBLE
            binding!!.linearAccpeted.visibility = View.GONE
        }else{
            binding!!.textHistory.visibility = View.GONE
            binding!!.frameAnswer.visibility = View.GONE
            binding!!.buttonFilters.visibility = View.VISIBLE
            binding!!.linearAccpeted.visibility = View.VISIBLE
        }

        binding!!.buttonJoin.setOnClickListener {
            meetsViewModel.joinGroupMeeting(eventId!!, userTgId!!, MeetingIdEntity(meetingId))
        }

        meetsViewModel.joinGroupMeeting.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                binding!!.buttonJoin.visibility = View.GONE
                binding!!.progressAnswer.visibility = View.GONE
                binding!!.frameAnswer.visibility = View.GONE
                binding!!.linearAccpeted.visibility = View.VISIBLE
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

    }

}