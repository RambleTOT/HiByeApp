package ramble.sokol.hibyeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import ramble.sokol.hibyeapp.databinding.FragmentLoginBinding
import ramble.sokol.hibyeapp.databinding.FragmentNetworkingBinding

class NetworkingFragment : Fragment() {

    private var binding: FragmentNetworkingBinding? = null

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
    }

    private fun setChecked(selectedCheckBox: CustomCheckBox) {
        binding!!.customCheckBox1.setChecked(false)
        binding!!.customCheckBox2.setChecked(false)
        binding!!.customCheckBox3.setChecked(false)

        selectedCheckBox.setChecked(true)
    }

}