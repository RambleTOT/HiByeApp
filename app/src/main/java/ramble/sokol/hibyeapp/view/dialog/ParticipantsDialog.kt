package ramble.sokol.hibyeapp.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.view.adapters.ParticipantsDialogAdapter

class ParticipantsDialog : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ParticipantsDialogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_meet_participant, container, false)

        // Получаем массивы имен и описаний из аргументов
        val userNames = arguments?.getStringArrayList("userNames") ?: emptyList()
        val userDescriptions = arguments?.getStringArrayList("userDescriptions") ?: emptyList()

        // Настройка RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ParticipantsDialogAdapter(userNames, userDescriptions)
        recyclerView.adapter = adapter

        // Настройка кнопки закрытия
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.button_add_new_event_dialog).setOnClickListener {
            dismiss()
        }

        return view
    }

    companion object {
        fun newInstance(userNames: ArrayList<String>, userDescriptions: ArrayList<String>): ParticipantsDialog {
            val dialog = ParticipantsDialog()
            val args = Bundle()
            args.putStringArrayList("userNames", userNames)
            args.putStringArrayList("userDescriptions", userDescriptions)
            dialog.arguments = args
            return dialog
        }
    }
}