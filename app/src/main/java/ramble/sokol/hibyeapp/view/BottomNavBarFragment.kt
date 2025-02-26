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
import ramble.sokol.hibyeapp.databinding.FragmentBottomNavBarBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.dialog.AllEventsDialog
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory


class BottomNavBarFragment(
    val currentFragment: Fragment
) : Fragment() {

    private var binding: FragmentBottomNavBarBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var currentF: Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomNavBarBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentF = currentFragment
        tokenManager = TokenManager(requireActivity())
        val tgId = tokenManager.getTelegramId()
        val nameEvent = tokenManager.getEventName()
        binding!!.eventName.text = nameEvent
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)
        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

        binding!!.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navbar_schedule -> {
                    currentF = ScheduleFragment()
                    replaceFragment(ScheduleFragment())
                }
                R.id.navbar_networking -> {
                    currentF = NetworkingFragment()
                    replaceFragment(NetworkingFragment())
                }
                R.id.navbar_chats -> {
                    currentF = ChatsFragment()
                    replaceFragment(ChatsFragment())
                }
                else -> false
            }
            true
        }

        binding!!.bottomNavigationView.selectedItemId = R.id.navbar_networking

        replaceFragment(currentFragment)

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            if (events != null) {
                AllEventsDialog(events) { event ->
                    Log.d("MyLog", "Selected Event ID: ${event.eventId}, Schedule ID: ${event.scheduleId}")
                    refreshFragment()
                }.show(parentFragmentManager, "EventsDialog")
            }
        })

        binding!!.buttonNewAddEvent.setOnClickListener {
            binding!!.buttonNewAddEvent.startAnimation(scaleDown)
            binding!!.buttonNewAddEvent.startAnimation(scaleUp)
            eventViewModel.fetchEvents(tgId!!)
        }

    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = parentFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
        fragmentTransition.commit()

    }

    private fun refreshFragment() {
        // Обновляем фрагмент
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, BottomNavBarFragment(currentF)) // Заменяем текущий фрагмент на новый экземпляр
            addToBackStack(null) // Добавляем в back stack (опционально)
            commit() // Фиксируем транзакцию
        }
    }

}