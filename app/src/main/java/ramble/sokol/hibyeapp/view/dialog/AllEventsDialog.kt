package ramble.sokol.hibyeapp.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.api.EventsApi
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.view.adapters.AllEventsAdapter

class AllEventsDialog(
    private val events: List<EventsEntity>,
    private val onItemClick: (EventsEntity) -> Unit
) : DialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_new_event, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEvents)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AllEventsAdapter(events){ event ->
            dismiss()
            onItemClick(event)
        }

        val buttonCode = view.findViewById<MaterialButton>(R.id.button_add_new_event_dialog)
        buttonCode.setOnClickListener {
            AddEventsDialog(){
                dismiss() // Закрываем первое диалоговое окно
                onItemClick(events[0]) // Вызываем лямбду для обновления фрагмента
            }.show(parentFragmentManager, "Code5Dialog")
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}