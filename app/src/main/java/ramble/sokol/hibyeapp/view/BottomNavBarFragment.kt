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
import ramble.sokol.hibyeapp.managers.EmptyEventsManager
import ramble.sokol.hibyeapp.managers.NameAndPhotoManager
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.dialog.AllEventsDialog
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory


class BottomNavBarFragment(
    val currentFragment: Fragment,
) : Fragment() {

    private var binding: FragmentBottomNavBarBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var nameAndPhotoManager: NameAndPhotoManager
    private lateinit var emptyEventsManager: EmptyEventsManager
    private lateinit var currentF: Fragment

    // Переменная для хранения текущего выбранного элемента BottomNavBar
    private var selectedNavItemId: Int = R.id.navbar_networking

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomNavBarBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyEventsManager = EmptyEventsManager(requireActivity())
        currentF = currentFragment
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)
        tokenManager = TokenManager(requireActivity())
        val tgId = tokenManager.getTelegramId()

        // Восстанавливаем выбранный элемент BottomNavBar
        if (savedInstanceState != null) {
            selectedNavItemId = savedInstanceState.getInt("selectedNavItemId", R.id.navbar_networking)
        }

        setSelectedNavigationIcon(currentFragment)

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            if (events != null) {
                if (emptyEventsManager.getIsEvent() == true) {
                    AllEventsDialog(events) { event ->
                        Log.d(
                            "MyLog",
                            "Selected Event ID: ${event.eventId}, Schedule ID: ${event.scheduleId}"
                        )
                        refreshFragment()
                    }.show(parentFragmentManager, "EventsDialog")
                } else {
                    // Показываем диалоговое окно
                    val dialog = AllEventsDialog(events) { event ->
                        Log.d("MyLog", "Selected Event ID: ${event.eventId}, Schedule ID: ${event.scheduleId}")
                        blockScreen(false)
                        refreshFragment()
                    }

                    emptyEventsManager.saveIsEvent(true)
                    dialog.isCancelable = false
                    dialog.show(parentFragmentManager, "EventsDialog")
                }
            }
        })

        if (emptyEventsManager.getIsEvent() == false) {
            blockScreen(true)
            eventViewModel.fetchEvents(tgId!!)
        } else {
            nameAndPhotoManager = NameAndPhotoManager(requireActivity())
            val nameEvent = tokenManager.getEventName()
            val eventId = tokenManager.getCurrentEventId()
            val userId = tokenManager.getUserIdTelegram()
            binding!!.eventName.text = nameEvent
            val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
            val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)

            eventViewModel.getUser(
                eventId = eventId!!,
                userId = userId!!
            )

            eventViewModel.getUser.observe(viewLifecycleOwner, Observer { result ->
                if (result.isSuccess) {
                    Log.d("MyLog", "Res^ $result")
                    val res = result.getOrNull()
                    nameAndPhotoManager.saveName(res!!.userName.toString())
                    nameAndPhotoManager.saveAbout(res.userInfo.toString())
                    nameAndPhotoManager.saveRequest(res.request.toString())
                } else if (result.isFailure) {
                    if (result.toString() == "Failure(java.lang.Exception: 400)") {
                        val transaction =
                            requireActivity().supportFragmentManager.beginTransaction()
                        val createUserFragment = CreateUserFragment()
                        transaction.replace(R.id.layout_fragment, createUserFragment)
                        transaction.disallowAddToBackStack()
                        transaction.commit()
                    }
                    val exception = result.exceptionOrNull()
                    //Toast.makeText(context, "Login failed: ${exception!!.message}", Toast.LENGTH_SHORT).show()
                }
            })

            binding!!.buttonNewAddEvent.setOnClickListener {
                binding!!.buttonNewAddEvent.startAnimation(scaleDown)
                binding!!.buttonNewAddEvent.startAnimation(scaleUp)
                eventViewModel.fetchEvents(tgId!!)
            }

            binding!!.bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.navbar_schedule -> {
                        currentF = ScheduleFragment()
                        replaceFragment(ScheduleFragment())
                        selectedNavItemId = R.id.navbar_schedule // Сохраняем выбранный элемент
                    }

                    R.id.navbar_networking -> {
                        currentF = NetworkingFragment()
                        replaceFragment(NetworkingFragment())
                        selectedNavItemId = R.id.navbar_networking // Сохраняем выбранный элемент
                    }

                    R.id.navbar_chats -> {
                        currentF = ChatsFragment()
                        replaceFragment(ChatsFragment())
                        selectedNavItemId = R.id.navbar_chats // Сохраняем выбранный элемент
                    }

                    else -> false
                }
                true
            }

            // Восстанавливаем выбранный элемент BottomNavBar
            binding!!.bottomNavigationView.selectedItemId = selectedNavItemId

            replaceFragment(currentFragment)

            binding!!.buttonNewAddEvent.setOnClickListener {
                binding!!.buttonNewAddEvent.startAnimation(scaleDown)
                binding!!.buttonNewAddEvent.startAnimation(scaleUp)
                eventViewModel.fetchEvents(tgId!!)
            }

            binding!!.buttonProfile.setOnClickListener {
                binding!!.buttonNewAddEvent.startAnimation(scaleDown)
                binding!!.buttonNewAddEvent.startAnimation(scaleUp)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val profileFragment = ProfileFragment(currentF)
                transaction.replace(R.id.layout_fragment, profileFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем выбранный элемент BottomNavBar
        outState.putInt("selectedNavItemId", selectedNavItemId)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
        fragmentTransition.commit()
    }

    private fun refreshFragment() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, BottomNavBarFragment(currentF))
            addToBackStack(null)
            commit()
        }
    }

    private fun blockScreen(isBlocked: Boolean) {
        if (isBlocked) {
            binding?.overlay?.visibility = View.VISIBLE
            binding?.overlay?.isClickable = true
            binding?.overlay?.isFocusable = true
        } else {
            binding?.overlay?.visibility = View.GONE
            binding?.overlay?.isClickable = false
            binding?.overlay?.isFocusable = false
        }
    }

    private fun setSelectedNavigationIcon(fragment: Fragment) {
        when (fragment) {
            is ScheduleFragment -> binding?.bottomNavigationView?.selectedItemId = R.id.navbar_schedule
            is NetworkingFragment -> binding?.bottomNavigationView?.selectedItemId = R.id.navbar_networking
            is ChatsFragment -> binding?.bottomNavigationView?.selectedItemId = R.id.navbar_chats
            else -> binding?.bottomNavigationView?.selectedItemId = R.id.navbar_networking
        }
    }
}