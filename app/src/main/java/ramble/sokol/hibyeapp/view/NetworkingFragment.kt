package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ramble.sokol.hibyeapp.CustomCheckBox
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.databinding.FragmentNetworkingBinding
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.FirstItemMarginDecoration
import ramble.sokol.hibyeapp.view.adapters.MeetsAdapter
import ramble.sokol.hibyeapp.view.adapters.ParticipantsAdapter
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory
import ramble.sokol.hibyeapp.view_model.MeetsViewModel
import ramble.sokol.hibyeapp.view_model.MeetsViewModelFactory

class NetworkingFragment : Fragment() {

    private var binding: FragmentNetworkingBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var meetsViewModel: MeetsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var participantsAdapter: ParticipantsAdapter
    private lateinit var meetsAdapter: MeetsAdapter
    private var isViewButtonFastMeetings: Boolean = false
    private lateinit var listMeets: List<MeetingResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNetworkingBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        listMeets = listOf()
        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userId = tokenManager.getUserIdTelegram()!!
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)

        meetsViewModel = ViewModelProvider(
            this,
            MeetsViewModelFactory((requireActivity().application as MyApplication).meetsRepository)
        ).get(MeetsViewModel::class.java)

        participantsAdapter = ParticipantsAdapter(emptyList()) { participant ->
            navigateToParticipantDetails(participant)
        }

        meetsAdapter = MeetsAdapter(emptyList()) { meet ->
            navigateToMeetDetails(meet)
        }


        binding?.recyclerViewSections?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = participantsAdapter
            val marginStart = resources.getDimensionPixelSize(R.dimen.margin_start) // 16dp
            addItemDecoration(FirstItemMarginDecoration(marginStart))
        }

        binding?.recyclerViewMeets?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = meetsAdapter
        }



        eventViewModel.getAllUsersEvent(eventId!!)
        eventViewModel.getAllUsersEvent.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val participants = result.getOrNull() ?: emptyList()
                participantsAdapter = ParticipantsAdapter(participants) { participant ->
                    navigateToParticipantDetails(participant)
                }
                binding?.recyclerViewSections?.adapter = participantsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        meetsViewModel.getAllMeets(eventId, userId)
        meetsViewModel.getAllMeets.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val meets = result.getOrNull() ?: emptyList()
                listMeets = meets
                meetsAdapter = MeetsAdapter(meets) { meet ->
                    navigateToMeetDetails(meet)
                }
                binding?.recyclerViewMeets?.adapter = meetsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading meets: ${exception?.message}")
            }
        })

        meetsViewModel.isFastMeetings(eventId, userId)
        meetsViewModel.isFastMeetings.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                isViewButtonFastMeetings = result.getOrNull()!!
                if (isViewButtonFastMeetings){
                    binding!!.buttonFindChat.visibility = View.VISIBLE
                }else{
                    binding!!.buttonFindChat.visibility = View.GONE
                }

            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        binding!!.customCheckBox1.findViewById<TextView>(R.id.checkbox_custom_text).text = "Мои встречи"
        binding!!.customCheckBox2.findViewById<TextView>(R.id.checkbox_custom_text).text = "Доступные встречи"
        binding!!.customCheckBox3.findViewById<TextView>(R.id.checkbox_custom_text).text = "История"

        binding!!.customCheckBox1.setOnClickListener {
            setChecked(binding!!.customCheckBox1)
        }
        binding!!.customCheckBox2.setOnClickListener {
            setChecked(binding!!.customCheckBox2)
        }
        binding!!.customCheckBox3.setOnClickListener {
            setChecked(binding!!.customCheckBox3)
        }

        setChecked(binding!!.customCheckBox1)

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding!!.textButtonAllParticipant.setOnClickListener {
            binding!!.textButtonAllParticipant.startAnimation(scaleDown)
            binding!!.textButtonAllParticipant.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val participantFragment = ParticipantFragment()
            transaction.replace(R.id.layout_fragment, participantFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
        binding!!.textButtonCreate.setOnClickListener {
            binding!!.textButtonAllParticipant.startAnimation(scaleDown)
            binding!!.textButtonAllParticipant.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val newEventFragment = NewEventFragment()
            transaction.replace(R.id.layout_fragment, newEventFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }

    private fun setChecked(selectedCheckBox: CustomCheckBox) {
        binding!!.customCheckBox1.setChecked(false)
        binding!!.customCheckBox2.setChecked(false)
        binding!!.customCheckBox3.setChecked(false)

        selectedCheckBox.setChecked(true)
    }

    fun findCommonMeeting(participantUserId: Long, meets: List<MeetingResponse>): MeetingResponse? {
        return meets.find { meet ->
            meet.userIds?.contains(participantUserId) == true
        }
    }

    fun findOrgMeeting(participantUserId: Long, meets: List<MeetingResponse>): MeetingResponse? {
        return meets.find { meet ->
            meet.organisatorId == participantUserId
        }
    }

    private fun navigateToParticipantDetails(participant: CreateUserResponse) {

        val bundle = Bundle().apply {

            if (findCommonMeeting(participant.userId!!, listMeets) == null){
                putBoolean("isMeet", false)
            }else{
                val stat = findCommonMeeting(participant.userId!!, listMeets)!!.meetingStatus
                putBoolean("isMeet", true)
                putString("isStat", stat)
                if (findOrgMeeting(participant.userId, listMeets) == null){
                    putBoolean("isOrg", false)
                }else{
                    putBoolean("isOrg", true)
                }
            }

            putLong("userId", participant.userId ?: -1)
            putString("userName", participant.userName)
            putString("userInfo", participant.userInfo)
            putString("photoLink", participant.photoLink)
        }

        val participantDetailsFragment = QuickMeetFragment(NetworkingFragment()).apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, participantDetailsFragment)
            addToBackStack(null)
            commit()
        }

    }

    private fun navigateToMeetDetails(meet: MeetingResponse) {

        val currentUserId = tokenManager.getUserIdTelegram()!!
        val secondUserId = meet.userIds?.firstOrNull { it != currentUserId }

        val bundle = Bundle().apply {
            putLong("meetingId", meet.meetingId ?: -1)
            putString("meetName", meet.name)
            putString("meetDescription", meet.description)
            putString("meetTime", meet.timeStart)
            putLong("organisatorId", meet.organisatorId ?: -1)
            putString("status", meet.meetingStatus)
            putLong("userIdOur", currentUserId)
            putLong("userIdSecond", secondUserId ?: -1)
        }

        val participantDetailsFragment = QuickMeetFragment(NetworkingFragment()).apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, participantDetailsFragment)
            addToBackStack(null)
            commit()
        }

    }

}