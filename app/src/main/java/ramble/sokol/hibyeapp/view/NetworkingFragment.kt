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
    private lateinit var allUsers: List<CreateUserResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        allUsers = listOf()
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


        binding?.recyclerViewSections?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = participantsAdapter
            val marginStart = resources.getDimensionPixelSize(R.dimen.margin_start) // 16dp
            addItemDecoration(FirstItemMarginDecoration(marginStart))
        }



        eventViewModel.getAllUsersEvent(eventId!!)
        eventViewModel.getAllUsersEvent.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                allUsers = result.getOrNull() ?: emptyList()
                val participants = result.getOrNull() ?: emptyList()
                val filteredParticipants = participants.filter { it.userId != userId }
                participantsAdapter = ParticipantsAdapter(filteredParticipants) { participant ->
                    navigateToParticipantDetails(participant)
                }
                binding?.recyclerViewSections?.adapter = participantsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        meetsAdapter = MeetsAdapter(emptyList(),allUsers,
            { meet -> navigateToMeetDetails(meet) },
            { meet -> navigateToGroupMeetDetails(meet) }
        )

        binding?.recyclerViewMeets?.apply {
            layoutManager = NonScrollLinearLayoutManager(requireContext())
            adapter = meetsAdapter
            isNestedScrollingEnabled = false
        }

        setupCheckBoxes(eventId, userId)

        meetsViewModel.getAllMeets(eventId, userId)
        meetsViewModel.getAllMeets.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val meets = result.getOrNull() ?: emptyList()
                listMeets = meets
                meetsAdapter = MeetsAdapter(meets, allUsers,
                    { meet -> navigateToMeetDetails(meet) },
                    { meet -> navigateToGroupMeetDetails(meet) }
                )
                binding?.recyclerViewMeets?.adapter = meetsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading meets: ${exception?.message}")
            }
        })

        loadAllMeets(eventId, userId)

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
            binding!!.textButtonCreate.startAnimation(scaleDown)
            binding!!.textButtonCreate.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val newEventFragment = NewEventFragment()
            transaction.replace(R.id.layout_fragment, newEventFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
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

            val meetCommon = findCommonMeeting(participant.userId!!, listMeets)

            if (meetCommon == null){
                putBoolean("isMeet", false)
            }else{
                val stat = meetCommon.meetingStatus
                putBoolean("isMeet", true)
                putLong("meetId", meetCommon.meetingId ?: -1)
                putLong("chatId", meetCommon.chatId ?: -1)
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
            putString("nameChat", participant.userName)
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

    private fun navigateToMeetDetails(meet: MeetingResponse, isAvailable: Boolean = false, isHistory: Boolean = false) {
        val currentUserId = tokenManager.getUserIdTelegram()!!
        val secondUserId = meet.userIds?.firstOrNull { it != currentUserId }



        val secondUser = allUsers.find { it.userId == secondUserId }

        val photoLink = secondUser?.photoLink

        val bundle = Bundle().apply {
            putLong("meetingId", meet.meetingId ?: -1)
            putString("meetName", meet.name)
            putLong("chatId", meet.chatId ?: -1)
            putString("meetDescription", meet.description)
            putString("meetTime", meet.timeStart)
            putLong("organisatorId", meet.organisatorId ?: -1)
            putString("status", meet.meetingStatus)
            putLong("userIdOur", currentUserId)
            putLong("userIdSecond", secondUserId ?: -1)
            putString("nameChat", meet.name)
            putBoolean("isAvailable", isAvailable)
            putBoolean("isHistory", isHistory)
            Log.d("MyLog", photoLink.toString())
            putString("photoLink", photoLink)
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

    private fun navigateToGroupMeetDetails(meet: MeetingResponse, isAvailable: Boolean = false, isHistory: Boolean = false) {
        // Получаем список всех пользователей мероприятия
        val allUsers = eventViewModel.getAllUsersEvent.value?.getOrNull() ?: emptyList()

        val filteredUsers = allUsers.filter { user ->
            meet.userIds?.contains(user.userId) == true
        }

        val userNames = filteredUsers.map { it.userName ?: "Unknown" }
        val userDescriptions = filteredUsers.map { it.userInfo ?: "No description" }
        val photoLink = filteredUsers.map { it.photoLink ?: "No description" }

        val bundle = Bundle().apply {
            putLong("meetingId", meet.meetingId ?: -1)
            putString("meetName", meet.name)
            putString("meetDescription", meet.description)
            putString("meetTime", meet.timeStart)
            putLong("organisatorId", meet.organisatorId ?: -1)
            putString("status", meet.meetingStatus)
            putString("count", meet.capacity.toString())
            putBoolean("isAvailable", isAvailable)
            putBoolean("isHistory", isHistory)
            putString("countSize", meet.userIds!!.size.toString())
            putStringArrayList("userNames", ArrayList(userNames))
            putStringArrayList("userDescriptions", ArrayList(userDescriptions))
            putStringArrayList("photoLink", ArrayList(photoLink))
        }

        val participantDetailsFragment = GroupMeetFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, participantDetailsFragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun loadAllMeets(eventId: Long, userId: Long) {
        meetsViewModel.getAllMeets(eventId, userId)
        meetsViewModel.getAllMeets.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val meets = result.getOrNull() ?: emptyList()
                listMeets = meets
                meetsAdapter = MeetsAdapter(meets, allUsers,
                    { meet -> navigateToMeetDetails(meet) },
                    { meet -> navigateToGroupMeetDetails(meet) }
                )
                binding?.recyclerViewMeets?.adapter = meetsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading meets: ${exception?.message}")
            }
        })
    }

    private fun loadAvailableMeets(eventId: Long, userId: Long) {
        meetsViewModel.getAllAvailableMeets(eventId, userId)
        meetsViewModel.getAllAvailableMeets.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val meets = result.getOrNull() ?: emptyList()
                listMeets = meets
                meetsAdapter = MeetsAdapter(meets, allUsers,
                    { meet -> navigateToMeetDetails(meet, isAvailable = true) },
                    { meet -> navigateToGroupMeetDetails(meet, isAvailable = true) }
                )
                binding?.recyclerViewMeets?.adapter = meetsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading available meets: ${exception?.message}")
            }
        })
    }

    private fun loadEndedMeets(eventId: Long, userId: Long) {
        meetsViewModel.getAllEndedMeets(eventId, userId)
        meetsViewModel.getAllEndedMeets.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val meets = result.getOrNull() ?: emptyList()
                listMeets = meets
                meetsAdapter = MeetsAdapter(meets, allUsers,
                    { meet -> navigateToMeetDetails(meet, isHistory = true) },
                    { meet -> navigateToGroupMeetDetails(meet, isHistory = true) }
                )
                binding?.recyclerViewMeets?.adapter = meetsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading ended meets: ${exception?.message}")
            }
        })
    }

    private fun setupCheckBoxes(eventId: Long, userId: Long) {
        binding!!.customCheckBox1.findViewById<TextView>(R.id.checkbox_custom_text).text = "Мои встречи"
        binding!!.customCheckBox2.findViewById<TextView>(R.id.checkbox_custom_text).text = "Доступные встречи"
        binding!!.customCheckBox3.findViewById<TextView>(R.id.checkbox_custom_text).text = "История"

        // Устанавливаем слушатели для чекбоксов
        binding!!.customCheckBox1.setOnCheckedChangeListener { isChecked ->
            if (isChecked) {
                setChecked(binding!!.customCheckBox1)
                loadAllMeets(eventId, userId)
            }
        }

        binding!!.customCheckBox2.setOnCheckedChangeListener { isChecked ->
            if (isChecked) {
                setChecked(binding!!.customCheckBox2)
                loadAvailableMeets(eventId, userId)
            }
        }

        binding!!.customCheckBox3.setOnCheckedChangeListener { isChecked ->
            if (isChecked) {
                setChecked(binding!!.customCheckBox3)
                loadEndedMeets(eventId, userId)
            }
        }

        // По умолчанию активен первый чекбокс
        setChecked(binding!!.customCheckBox1)
    }

    private fun setChecked(selectedCheckBox: CustomCheckBox) {
        binding!!.customCheckBox1.setChecked(selectedCheckBox == binding!!.customCheckBox1)
        binding!!.customCheckBox2.setChecked(selectedCheckBox == binding!!.customCheckBox2)
        binding!!.customCheckBox3.setChecked(selectedCheckBox == binding!!.customCheckBox3)
    }

}