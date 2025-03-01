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
import ramble.sokol.hibyeapp.databinding.FragmentProfileBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view_model.EventsViewModel
import ramble.sokol.hibyeapp.view_model.EventsViewModelFactory

class ProfileFragment(
    val currentFragment: Fragment
) : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var tokenManager: TokenManager
    private var name = ""
    private var request = ""
    private var about = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireActivity())
        val eventId = tokenManager.getCurrentEventId()
        val userId = tokenManager.getUserIdTelegram()
        eventViewModel = ViewModelProvider(
            this,
            EventsViewModelFactory((requireActivity().application as MyApplication).eventsRepository)
        ).get(EventsViewModel::class.java)

        eventViewModel.getUser(
            eventId = eventId!!,
            userId = userId!!
        )


        binding!!.editTextName.isEnabled = false
        binding!!.editTextRequest.isEnabled = false
        binding!!.editTextAboutMe.isEnabled = false

        eventViewModel.getUser.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val res = result.getOrNull()
                Log.d("MyLog", "Res^ ${res!!.userInfo}")
                name = res!!.userName.toString()
                about = res.userInfo.toString()
                request = res.request.toString()
                binding!!.editTextName.setText(name)
                binding!!.editTextRequest.setText(request)
                binding!!.editTextAboutMe.setText(about)
            } else if (result.isFailure) {
                if (result.toString() == "Failure(java.lang.Exception: 400)"){
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    val createUserFragment = CreateUserFragment()
                    transaction.replace(R.id.layout_fragment, createUserFragment)
                    transaction.disallowAddToBackStack()
                    transaction.commit()
                }
                val exception = result.exceptionOrNull()
                //Toast.makeText(context, "Login failed: ${exception!!.message}", Toast.LENGTH_SHORT).show()
            }

        })


        val scaleDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim)
        val scaleUp = AnimationUtils.loadAnimation(requireActivity(), R.anim.text_click_anim_back)
        binding!!.textButtonBack.setOnClickListener{
            binding!!.textButtonBack.startAnimation(scaleDown)
            binding!!.textButtonBack.startAnimation(scaleUp)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val profileFragment = BottomNavBarFragment(currentFragment)
            transaction.replace(R.id.layout_fragment, profileFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }

    }

}