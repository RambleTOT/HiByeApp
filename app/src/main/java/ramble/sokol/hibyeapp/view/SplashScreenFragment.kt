package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.FragmentSplashScreenBinding
import ramble.sokol.hibyeapp.managers.ProfileAndCodeManager
import ramble.sokol.hibyeapp.view_model.AuthViewModel
import ramble.sokol.hibyeapp.view_model.AuthViewModelFactory

class SplashScreenFragment : Fragment() {

    private var binding: FragmentSplashScreenBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileAndCodeManager: ProfileAndCodeManager

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
        val fadeInAnimation = AnimationUtils.loadAnimation(requireActivity(),
            R.anim.splash_screen_animation
        )
        binding!!.linearSplash.startAnimation(fadeInAnimation)

        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory((requireActivity().application as MyApplication).authRepository)
        ).get(AuthViewModel::class.java)

        profileAndCodeManager = ProfileAndCodeManager(requireActivity())

        checkTokenAndNavigate()

//        if (tokenManager.isLoggedIn()) {
//            Handler().postDelayed(Runnable {
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                val bottomNavigationFragment = BottomNavBarFragment(NetworkingFragment())
//                transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
//                transaction.disallowAddToBackStack()
//                transaction.commit()
//            }, 3000)
//        } else {
//            Handler().postDelayed(Runnable {
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                val loginFragment = LoginFragment()
//                transaction.replace(R.id.layout_fragment, loginFragment)
//                transaction.disallowAddToBackStack()
//                transaction.commit()
//            }, 3000)
//        }
//
//        //firstEntryManager = FirstEntryManager(requireActivity())
////        if (firstEntryManager.getFirstEntry() == true){
////            Handler().postDelayed(Runnable {
////                val transaction = requireActivity().supportFragmentManager.beginTransaction()
////                val bottomNavigationFragment = BottomNavigationFragment()
////                transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
////                transaction.disallowAddToBackStack()
////                transaction.commit()
////            }, 3000)
////        }else{
////            Handler().postDelayed(Runnable {
////                val transaction = requireActivity().supportFragmentManager.beginTransaction()
////                val loginFragment = LoginFragment()
////                transaction.replace(R.id.layout_fragment, loginFragment)
////                transaction.disallowAddToBackStack()
////                transaction.commit()
////            }, 3000)
////        }
//
//        Handler().postDelayed(Runnable {
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            val loginFragment = LoginFragment()
//            transaction.replace(R.id.layout_fragment, loginFragment)
//            transaction.disallowAddToBackStack()
//            transaction.commit()
//        }, 3000)

    }

    private fun checkTokenAndNavigate() {
        val tokenManager = (requireActivity().application as MyApplication).tokenManager

        if (tokenManager.isLoggedIn()) {
            authViewModel.refreshToken()
            authViewModel.refreshTokenResult.observe(viewLifecycleOwner, Observer { result ->
                Log.d("MyLog", "${result}")
                if (result.isSuccess){
                    if (profileAndCodeManager.getRegistr() == true){
                        if (profileAndCodeManager.getProfile() == true){
                            if (profileAndCodeManager.getCode() == true){
                                navigateToHome()
                            }else{
                                navigateToCode()
                            }
                        }else{
                            navigateToProfile()
                        }
                    }else {
                        navigateToHome()
                    }
                }else if (result.isFailure){
                    navigateToLogin()
                }
            })
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToHome() {
        Handler().postDelayed(Runnable {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val bottomNavigationFragment = BottomNavBarFragment(NetworkingFragment())
                transaction.replace(R.id.layout_fragment, bottomNavigationFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }, 3000)
    }

    private fun navigateToProfile() {
        Handler().postDelayed(Runnable {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val profileFragment = CreateProfileFragment()
            transaction.replace(R.id.layout_fragment, profileFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }, 3000)
    }

    private fun navigateToCode() {
        Handler().postDelayed(Runnable {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val codeEventFragment = CodeEventFragment()
            transaction.replace(R.id.layout_fragment, codeEventFragment)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }, 3000)
    }

    private fun navigateToLogin() {
        Handler().postDelayed(Runnable {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                val loginFragment = LoginFragment()
                transaction.replace(R.id.layout_fragment, loginFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }, 3000)
    }


}