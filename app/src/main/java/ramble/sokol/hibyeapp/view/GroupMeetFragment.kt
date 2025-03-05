package ramble.sokol.hibyeapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentGroupMeetBinding
import ramble.sokol.hibyeapp.databinding.FragmentQuickMeetBinding
import ramble.sokol.hibyeapp.managers.TokenManager
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


        val meetName = arguments?.getString("meetName", "") ?: ""
        val meetDescription = arguments?.getString("meetDescription", "") ?: ""
        val meetTime = arguments?.getString("meetTime", "") ?: ""
        val organisatorId = arguments?.getLong("organisatorId", -1) ?: -1
        val meetingId = arguments?.getLong("meetingId", -1) ?: -1
        val status = arguments?.getString("status", "") ?: ""
        val count = arguments?.getString("count", "") ?: ""

        binding!!.descriptionMeet.text = meetDescription
        binding!!.name.text = meetName

    }

}