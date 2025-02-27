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
import androidx.recyclerview.widget.LinearLayoutManager
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.databinding.FragmentNetworkingBinding
import ramble.sokol.hibyeapp.databinding.FragmentParticipantBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.AllParticipantsAdapter
import ramble.sokol.hibyeapp.view.adapters.ParticipantsAdapter
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory

class ParticipantFragment : Fragment() {

    private var binding: FragmentParticipantBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var participantsAdapter: AllParticipantsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

        binding!!.textButtonBack.setOnClickListener {
            binding!!.textButtonBack.startAnimation(scaleDown)
            binding!!.textButtonBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val bottomNavigationFragment = BottomNavBarFragment(NetworkingFragment())
            transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)

        participantsAdapter = AllParticipantsAdapter(emptyList()) { participant ->
            navigateToParticipantDetails(participant)
        }

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = participantsAdapter
        }

        eventViewModel.getAllUsersEvent(eventId!!)
        eventViewModel.getAllUsersEvent.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val participants = result.getOrNull() ?: emptyList()
                participantsAdapter = AllParticipantsAdapter(participants) { participant ->
                    navigateToParticipantDetails(participant)
                }
                binding?.recyclerView?.adapter = participantsAdapter
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

    }

    private fun navigateToParticipantDetails(participant: CreateUserResponse) {

        val bundle = Bundle().apply {
            putLong("userId", participant.userId ?: -1)
            putString("userName", participant.userName)
            putString("userInfo", participant.userInfo)
            putString("photoLink", participant.photoLink)
        }

        val participantDetailsFragment = ParticipantFragment().apply {
            arguments = bundle
        }

    }

}