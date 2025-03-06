package ramble.sokol.hibyeapp.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.databinding.DialogFilterBinding
import ramble.sokol.hibyeapp.view.adapters.FilterAdapter

class FilterDialogFragment(
    private val tags: List<String>,
    private val selectedTags: List<String>, // Добавляем параметр для выбранных тегов
    private val onApplyFilters: (List<String>) -> Unit,
    private val onClearFilters: () -> Unit
) : DialogFragment() {

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    private lateinit var filterAdapter: FilterAdapter

    // Сохраняем выбранные теги
    private val currentSelectedTags = selectedTags.toMutableSet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Восстанавливаем выбранные теги, если они были сохранены
        if (savedInstanceState != null) {
            val savedTags = savedInstanceState.getStringArrayList("selectedTags")
            if (savedTags != null) {
                currentSelectedTags.clear()
                currentSelectedTags.addAll(savedTags)
            }
        }

        // Передаем текущие выбранные теги в адаптер
        filterAdapter = FilterAdapter(tags, currentSelectedTags.toList()) { selectedTags ->
            // Обновляем выбранные теги
            currentSelectedTags.clear()
            currentSelectedTags.addAll(selectedTags)
        }

        binding.recyclerViewFilters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = filterAdapter
        }

        binding.buttonApplyFilters.setOnClickListener {
            onApplyFilters(currentSelectedTags.toList())
            dismiss()
        }

        binding.buttonClearFilters.setOnClickListener {
            currentSelectedTags.clear()
            onClearFilters()
            dismiss()
        }
    }

    // Сохраняем выбранные теги при уничтожении диалога
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("selectedTags", ArrayList(currentSelectedTags))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}