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
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.data.model.meets.SendMeetingRequestEntity
import ramble.sokol.hibyeapp.databinding.FragmentQuickMeetBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.ParticipantsAdapter
import ramble.sokol.hibyeapp.view_model.MeetsViewModel
import ramble.sokol.hibyeapp.view_model.MeetsViewModelFactory

class QuickMeetFragment(
    val lastFragment: Fragment
) : Fragment() {

    private var binding: FragmentQuickMeetBinding? = null
    private lateinit var meetsViewModel: MeetsViewModel
    private lateinit var tokenManager: TokenManager

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

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userTgId = tokenManager.getUserIdTelegram()

        meetsViewModel = ViewModelProvider(
            this,
            MeetsViewModelFactory((requireActivity().application as MyApplication).meetsRepository)
        ).get(MeetsViewModel::class.java)

        var userId = arguments?.getLong("userId", -1) ?: -1
        var meetingId = arguments?.getLong("meetingId", -1) ?: -1
        val meetId = arguments?.getLong("meetId", -1) ?: -1
        if (userId != -1L) {
            val userName = arguments?.getString("userName", "") ?: ""
            val userInfo = arguments?.getString("userInfo", "") ?: ""
            val photoLink = arguments?.getString("photoLink", "") ?: ""
            val isMeet = arguments?.getBoolean("isMeet", false)
            val isOrg = arguments?.getBoolean("isOrg", false)
            val isStat = arguments?.getString("isStat", "") ?: ""

            binding!!.name.text = userName
            binding!!.descriptionMeet.text = userInfo

            binding!!.buttonSendRequest.setOnClickListener {
                binding!!.buttonSendRequest.visibility = View.INVISIBLE
                binding!!.progressLogin.visibility = View.VISIBLE
                meetsViewModel.sendRequestMeeting(eventId!!, SendMeetingRequestEntity(
                    eventId = eventId,
                    idFrom = userTgId,
                    idTo = userId,
                    status = "AWAITING_RESPONSE",
                    meetingType = "REQUEST"
                ))
            }

            if (isMeet == false) {
                binding!!.frameRequest.visibility = View.VISIBLE
                binding!!.frameAnswerNot.visibility = View.GONE
                binding!!.frameAnswer.visibility = View.GONE
                binding!!.linearAccepted.visibility = View.GONE
                binding!!.buttonSendRequest.visibility = View.VISIBLE
            } else {
                if (isStat == "ACCEPTED"){
                    binding!!.frameRequest.visibility = View.GONE
                    binding!!.frameAnswerNot.visibility = View.GONE
                    binding!!.frameAnswer.visibility = View.GONE
                    binding!!.linearAccepted.visibility = View.VISIBLE
                }else if (isStat == "REJECTED"){
                    binding!!.linearAccepted.visibility = View.GONE
                    binding!!.frameRequest.visibility = View.GONE
                    binding!!.frameAnswerNot.visibility = View.GONE
                    binding!!.frameAnswer.visibility = View.GONE
                    binding!!.textOrgNot.visibility = View.VISIBLE
                } else {
                    if (isOrg == true) {
                        binding!!.frameRequest.visibility = View.GONE
                        binding!!.linearAccepted.visibility = View.GONE
                        binding!!.frameAnswerNot.visibility = View.VISIBLE
                        binding!!.frameAnswer.visibility = View.VISIBLE
                        binding!!.buttonSendAnswerNot.visibility = View.VISIBLE
                        binding!!.buttonSendAnswer.visibility = View.VISIBLE
                    } else {
                        binding!!.frameRequest.visibility = View.VISIBLE
                        binding!!.frameAnswerNot.visibility = View.GONE
                        binding!!.frameAnswer.visibility = View.GONE
                        binding!!.linearAccepted.visibility = View.GONE
                        binding!!.textOrg.visibility = View.VISIBLE
                        binding!!.buttonSendRequest.visibility = View.INVISIBLE
                    }
                }
            }

            meetsViewModel.sendRequestMeeting.observe(viewLifecycleOwner, Observer { result ->
                if (result.isSuccess) {
                    binding!!.buttonSendRequest.visibility = View.INVISIBLE
                    binding!!.progressLogin.visibility = View.INVISIBLE
                    if (result.getOrNull()!!.meetingStatus == "REJECTED"){
                        binding!!.textOrgNot.visibility = View.VISIBLE
                    }else{
                        binding!!.textOrg.visibility = View.VISIBLE
                    }
                } else if (result.isFailure) {
                    binding!!.buttonSendRequest.visibility = View.VISIBLE
                    binding!!.progressLogin.visibility = View.INVISIBLE
                    binding!!.textOrg.visibility = View.INVISIBLE
                    val exception = result.exceptionOrNull()
                    Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
                }
            })


        }

        if (meetingId != -1L){
            userId = arguments?.getLong("userIdSecond", -1) ?: -1
            val meetName = arguments?.getString("meetName", "") ?: ""
            val meetDescription = arguments?.getString("meetDescription", "") ?: ""
            val meetTime = arguments?.getString("meetTime", "") ?: ""
            val organisatorId = arguments?.getLong("organisatorId", -1) ?: -1
            val userIdOur = arguments?.getLong("userIdOur", -1) ?: -1
            val status = arguments?.getString("status", "") ?: ""

            binding!!.name.text = meetName
            binding!!.descriptionMeet.text = meetDescription

            Log.d("MyLog", status.toString())

            if (status == "ACCEPTED"){
                binding!!.linearAccepted.visibility = View.VISIBLE
                binding!!.frameRequest.visibility = View.GONE
                binding!!.frameAnswerNot.visibility = View.GONE
                binding!!.frameAnswer.visibility = View.GONE
            }else if(status == "REJECTED"){
                binding!!.linearAccepted.visibility = View.GONE
                binding!!.frameRequest.visibility = View.GONE
                binding!!.frameAnswerNot.visibility = View.GONE
                binding!!.frameAnswer.visibility = View.GONE
                binding!!.textOrgNot.visibility = View.VISIBLE
            }else if (status == "AWAITING_RESPONSE"){
                if (userIdOur == organisatorId){
                    binding!!.linearAccepted.visibility = View.GONE
                    binding!!.frameRequest.visibility = View.VISIBLE
                    binding!!.frameAnswerNot.visibility = View.GONE
                    binding!!.frameAnswer.visibility = View.GONE
                    binding!!.textOrg.visibility = View.VISIBLE
                }else{
                    binding!!.linearAccepted.visibility = View.GONE
                    binding!!.frameRequest.visibility = View.GONE
                    binding!!.frameAnswerNot.visibility = View.VISIBLE
                    binding!!.frameAnswer.visibility = View.VISIBLE
                    binding!!.buttonSendAnswer.visibility = View.VISIBLE
                    binding!!.buttonSendAnswerNot.visibility = View.VISIBLE
                }
            }

        }

        binding!!.buttonSendAnswer.setOnClickListener {
            binding!!.frameAnswerNot.visibility = View.GONE
            binding!!.buttonSendAnswer.visibility = View.INVISIBLE
            binding!!.progressAnswer.visibility = View.VISIBLE
            meetsViewModel.sendAnswerMeeting(eventId!!, SendMeetingRequestEntity(
                eventId = eventId,
                idFrom = userId,
                idTo = userTgId,
                status = "ACCEPTED",
                meetingType = "REQUEST"
            ))
        }

        binding!!.buttonSendAnswerNot.setOnClickListener {
            binding!!.frameAnswer.visibility = View.GONE
            binding!!.buttonSendAnswerNot.visibility = View.INVISIBLE
            binding!!.progressAnswerNot.visibility = View.VISIBLE
            meetsViewModel.sendAnswerMeetingNot(eventId!!, SendMeetingRequestEntity(
                eventId = eventId,
                idFrom = userId,
                idTo = userTgId,
                status = "REJECTED",
                meetingType = "REQUEST"
            ))
        }

        meetsViewModel.sendAnswerMeeting.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                binding!!.buttonSendAnswer.visibility = View.INVISIBLE
                binding!!.progressAnswer.visibility = View.INVISIBLE
                binding!!.frameAnswer.visibility = View.GONE
                binding!!.frameAnswerNot.visibility = View.GONE
                binding!!.linearAccepted.visibility = View.VISIBLE
            } else if (result.isFailure) {
                binding!!.buttonSendAnswer.visibility = View.VISIBLE
                binding!!.buttonSendAnswerNot.visibility = View.VISIBLE
                binding!!.progressAnswer.visibility = View.INVISIBLE
                binding!!.frameAnswerNot.visibility = View.VISIBLE
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        meetsViewModel.sendAnswerMeetingNot.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                binding!!.buttonSendAnswerNot.visibility = View.INVISIBLE
                binding!!.progressAnswerNot.visibility = View.INVISIBLE
                binding!!.frameAnswer.visibility = View.GONE
                binding!!.frameAnswerNot.visibility = View.GONE
                binding!!.linearAccepted.visibility = View.GONE
                binding!!.textOrgNot.visibility = View.VISIBLE
            } else if (result.isFailure) {
                binding!!.buttonSendAnswer.visibility = View.VISIBLE
                binding!!.buttonSendAnswerNot.visibility = View.VISIBLE
                binding!!.progressAnswerNot.visibility = View.INVISIBLE
                binding!!.frameAnswerNot.visibility = View.VISIBLE
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

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

        binding!!.layotMeetEnd.setOnClickListener {
            binding!!.layotMeetEnd.startAnimation(scaleDown)
            binding!!.layotMeetEnd.startAnimation(scaleUp)
            if (meetingId == -1L){
                meetingId = meetId
            }
            binding!!.linearAccepted.visibility = View.GONE
            meetsViewModel.meetingFinished(eventId!!, MeetingIdEntity(meetingId))
        }

        binding!!.layotNotBegin.setOnClickListener {
            binding!!.layotNotBegin.startAnimation(scaleDown)
            binding!!.layotNotBegin.startAnimation(scaleUp)
            if (meetingId == -1L){
                meetingId = meetId
            }
            binding!!.linearAccepted.visibility = View.GONE
            meetsViewModel.meetingNotBegin(eventId!!, userTgId!!,  MeetingIdEntity(meetingId))
        }

        meetsViewModel.meetingNotBegin.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                //binding!!.linearAccepted.visibility = View.GONE
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        meetsViewModel.meetingFinished.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                //binding!!.linearAccepted.visibility = View.GONE
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

    }

}