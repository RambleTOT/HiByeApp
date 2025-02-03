package ramble.sokol.hibyeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ramble.sokol.hibyeapp.databinding.FragmentBottomNavBarBinding


class BottomNavBarFragment(
    val currentFragment: Fragment
) : Fragment() {

    private var binding: FragmentBottomNavBarBinding? = null
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

        replaceFragment(currentFragment)
        binding!!.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.navbar_schedule -> replaceFragment(ScheduleFragment())
                R.id.navbar_networking -> replaceFragment(NetworkingFragment())
                R.id.navbar_chats -> replaceFragment(ChatsFragment())
                else -> {}
            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = parentFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
        fragmentTransition.commit()

    }

}