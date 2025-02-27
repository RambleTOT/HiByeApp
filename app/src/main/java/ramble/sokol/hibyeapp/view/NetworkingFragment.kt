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
import ramble.sokol.hibyeapp.databinding.FragmentNetworkingBinding
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.FirstItemMarginDecoration
import ramble.sokol.hibyeapp.view.adapters.ParticipantsAdapter
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory

class NetworkingFragment : Fragment() {

    private var binding: FragmentNetworkingBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var participantsAdapter: ParticipantsAdapter

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

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)

        participantsAdapter = ParticipantsAdapter(emptyList()) { participant ->
            // Обработка нажатия на элемент
            navigateToParticipantDetails(participant)
        }

        binding?.recyclerViewSections?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = participantsAdapter
            // Добавляем кастомный ItemDecoration для отступа первого элемента
            val marginStart = resources.getDimensionPixelSize(R.dimen.margin_start) // 16dp
            addItemDecoration(FirstItemMarginDecoration(marginStart))
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

        eventViewModel.getAllUsersEvent.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                Log.d("MyLog", "$result")
            } else if (result.isFailure) {
                val exception = result.exceptionOrNull()
                //Toast.makeText(context, "Login failed: ${exception!!.message}", Toast.LENGTH_SHORT).show()
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