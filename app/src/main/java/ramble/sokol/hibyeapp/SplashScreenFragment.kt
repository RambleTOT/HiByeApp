package ramble.sokol.hibyeapp

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import ramble.sokol.hibyeapp.databinding.FragmentLoginBinding
import ramble.sokol.hibyeapp.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private var binding: FragmentSplashScreenBinding? = null
    //private lateinit var firstEntryManager: FirstEntryManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fadeInAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.splash_screen_animation)
        binding!!.linearSplash.startAnimation(fadeInAnimation)
        //firstEntryManager = FirstEntryManager(requireActivity())
//        if (firstEntryManager.getFirstEntry() == true){
//            Handler().postDelayed(Runnable {
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                val bottomNavigationFragment = BottomNavigationFragment()
//                transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
//                transaction.disallowAddToBackStack()
//                transaction.commit()
//            }, 3000)
//        }else{
//            Handler().postDelayed(Runnable {
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                val loginFragment = LoginFragment()
//                transaction.replace(R.id.layout_fragment, loginFragment)
//                transaction.disallowAddToBackStack()
//                transaction.commit()
//            }, 3000)
//        }

        Handler().postDelayed(Runnable {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val loginFragment = LoginFragment()
            transaction.replace(R.id.layout_fragment, loginFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }, 3000)

    }

}