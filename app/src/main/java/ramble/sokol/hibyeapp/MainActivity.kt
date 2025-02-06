package ramble.sokol.hibyeapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        val splashScreenFragment = SplashScreenFragment()
        val fragment : Fragment? =
            supportFragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        if (fragment !is SplashScreenFragment){
            supportFragmentManager.beginTransaction()
                .add(R.id.layout_fragment, splashScreenFragment, SplashScreenFragment::class.java.simpleName)
                .commit()
        }
    }
}