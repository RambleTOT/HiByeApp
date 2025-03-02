package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleItem
import ramble.sokol.hibyeapp.databinding.FragmentLoginBinding
import ramble.sokol.hibyeapp.databinding.FragmentScheduleBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.ParticipantsAdapter
import ramble.sokol.hibyeapp.view.adapters.ScheduleAdapter
import ramble.sokol.hibyeapp.view_model.AuthViewModel
import ramble.sokol.hibyeapp.view_model.AuthViewModelFactory
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory
import ramble.sokol.hibyeapp.view_model.ScheduleViewModel
import ramble.sokol.hibyeapp.view_model.ScheduleViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class ScheduleFragment : Fragment() {

    private var binding: FragmentScheduleBinding? = null
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var listFavorite: List<Long>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listFavorite = arrayListOf()

        scheduleViewModel = ViewModelProvider(
            this,
            ScheduleViewModelFactory((requireActivity().application as MyApplication).scheduleRepository)
        ).get(ScheduleViewModel::class.java)

        tokenManager = (requireActivity().application as MyApplication).tokenManager

        scheduleAdapter = ScheduleAdapter(emptyList()) { item ->
            navigateToScheduleDetails(item)
        }

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scheduleAdapter
        }

        val scheduleId = tokenManager.getCurrentScheduleId()
        scheduleViewModel.getSchedule(scheduleId!!)
        scheduleViewModel.getSchedule.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val scheduleResponse = result.getOrNull()
                Log.d("MyLog", "ScheduleResponse: $scheduleResponse")

                // Получаем элементы из `items`
                val items = scheduleResponse?.items ?: emptyList()

                val par = if (items.size >= 0) items[0].parentId
                else scheduleResponse!!.parentId

                scheduleAdapter = ScheduleAdapter(items) { item ->
                    navigateToScheduleDetails(item)
                }
                binding?.recyclerView?.adapter = scheduleAdapter

                Log.d("MyLog", "${par} ${tokenManager.getUserIdTelegram()}")
                scheduleViewModel.getFavorite(par!!, tokenManager.getUserIdTelegram()!!)

            } else if (result.isFailure) {
                Log.d("MyLog", "$result")
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })


        scheduleViewModel.getFavorite.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                Log.d("MyLog", result.toString())
                val favoriteScheduleIds = result.getOrNull() ?: emptyList()
                listFavorite = favoriteScheduleIds
            } else if (result.isFailure) {
                Log.d("MyLog", "$result")
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

    }

    private fun navigateToScheduleDetails(item: ScheduleItem) {
        // Переход на фрагмент с деталями элемента
        Log.d("MyLog", listFavorite.toString())
        val bundle = Bundle().apply {
            putLong("scheduleId", item.scheduleId ?: -1)
            putString("title", item.title)
            putString("timeStart", item.timeStart)
            putString("timeEnd", item.timeEnd)
            putString("description", item.description)
            putLong("parentId", item.parentId ?: -1)
            putStringArrayList("tags", ArrayList(item.tags))
            putLongArray("favoriteScheduleIds", listFavorite.toLongArray())



        }

        val detailsFragment = CurrentEventFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.layout_fragment, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

}