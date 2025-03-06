package ramble.sokol.hibyeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ramble.sokol.hibyeapp.CustomCheckBox
import ramble.sokol.hibyeapp.R
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleItem
import ramble.sokol.hibyeapp.databinding.FragmentScheduleBinding
import ramble.sokol.hibyeapp.managers.TokenManager
import ramble.sokol.hibyeapp.view.adapters.ScheduleAdapter
import ramble.sokol.hibyeapp.view.dialog.FilterDialogFragment
import ramble.sokol.hibyeapp.view_model.ScheduleViewModel
import ramble.sokol.hibyeapp.view_model.ScheduleViewModelFactory


class ScheduleFragment : Fragment() {

    private var binding: FragmentScheduleBinding? = null
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var listFavorite: List<Long>
    private var allItems: List<ScheduleItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listFavorite = arrayListOf()

        scheduleViewModel = ViewModelProvider(
            this,
            ScheduleViewModelFactory((requireActivity().application as MyApplication).scheduleRepository)
        ).get(ScheduleViewModel::class.java)

        tokenManager = (requireActivity().application as MyApplication).tokenManager

        scheduleAdapter = ScheduleAdapter(emptyList()) { item ->
            navigateToScheduleDetails(item)
        }

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scheduleAdapter
        }

        val scheduleId = tokenManager.getCurrentScheduleId()
        scheduleViewModel.getSchedule(scheduleId!!)
        scheduleViewModel.getSchedule.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val scheduleResponse = result.getOrNull()
                Log.d("MyLog", "ScheduleResponse: $scheduleResponse")

                allItems = scheduleResponse?.items ?: emptyList()

                val par = if (allItems.isNotEmpty()) allItems[0].parentId
                else scheduleResponse!!.parentId

                scheduleAdapter.updateData(allItems)

                binding?.recyclerView?.apply {
                    layoutManager = NonScrollLinearLayoutManager(requireContext())
                    adapter = scheduleAdapter
                    isNestedScrollingEnabled = false
                }

                Log.d("MyLog", "${par} ${tokenManager.getUserIdTelegram()}")
                scheduleViewModel.getFavorite(par!!, tokenManager.getUserIdTelegram()!!)

            } else if (result.isFailure) {
                Log.d("MyLog", "$result")
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        scheduleViewModel.getFavorite.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                Log.d("MyLog", result.toString())
                val favoriteScheduleIds = result.getOrNull() ?: emptyList()
                listFavorite = favoriteScheduleIds
            } else if (result.isFailure) {
                Log.d("MyLog", "$result")
                val exception = result.exceptionOrNull()
                Log.e("NetworkingFragment", "Error loading participants: ${exception?.message}")
            }
        })

        binding!!.buttonFilters.setOnClickListener {
            val tags = scheduleViewModel.getSchedule.value?.getOrNull()?.tags ?: emptyList()
            val selectedTags = scheduleViewModel.selectedTags.value ?: emptySet()
            val dialog = FilterDialogFragment(
                tags = tags,
                selectedTags = selectedTags.toList(), // Передаем выбранные теги
                onApplyFilters = { selectedTags ->
                    scheduleViewModel.setSelectedTags(selectedTags.toSet())
                    applyFilters() // Применяем фильтры после выбора тегов
                },
                onClearFilters = {
                    scheduleViewModel.setSelectedTags(emptySet())
                    applyFilters() // Применяем фильтры после сброса
                }
            )
            dialog.show(parentFragmentManager, "FilterDialogFragment")
        }
        setupCheckBoxes()
    }

    private fun setupCheckBoxes() {
        binding!!.customCheckBoxAll.findViewById<TextView>(R.id.checkbox_custom_text).text = "Все"
        binding!!.customCheckBoxFavorite.findViewById<TextView>(R.id.checkbox_custom_text).text = "Избранное"

        // Устанавливаем слушатели для чекбоксов
        binding!!.customCheckBoxAll.setOnCheckedChangeListener { isChecked ->
            if (isChecked) {
                setChecked(binding!!.customCheckBoxAll)
                applyFilters()
            }
        }

        binding!!.customCheckBoxFavorite.setOnCheckedChangeListener { isChecked ->
            if (isChecked) {
                setChecked(binding!!.customCheckBoxFavorite)
                applyFilters()
            }
        }

        // По умолчанию активен первый чекбокс
        setChecked(binding!!.customCheckBoxAll)
    }

    private fun applyFilters() {
        val selectedTags = scheduleViewModel.selectedTags.value ?: emptySet()

        // Если выбран чекбокс "Избранное", фильтруем только по избранным элементам
        if (binding!!.customCheckBoxFavorite.isChecked) {
            val favoriteItems = allItems.filter { item -> listFavorite.contains(item.scheduleId) }
            scheduleAdapter.updateData(favoriteItems)
        } else {
            // Иначе применяем фильтрацию по тегам
            val filteredItems = allItems.filter { item ->
                selectedTags.all { tag -> item.tags?.contains(tag) == true }
            }
            scheduleAdapter.updateData(filteredItems)
        }
    }

    private fun setChecked(selectedCheckBox: CustomCheckBox) {
        binding!!.customCheckBoxAll.setChecked(selectedCheckBox == binding!!.customCheckBoxAll)
        binding!!.customCheckBoxFavorite.setChecked(selectedCheckBox == binding!!.customCheckBoxFavorite)
    }



    private fun navigateToScheduleDetails(item: ScheduleItem) {
        // Переход на фрагмент с деталями элемента
        Log.d("MyLog", listFavorite.toString())
        val bundle = Bundle().apply {
            putLong("scheduleId", item.scheduleId ?: -1)
            putString("title", item.title)
            putString("timeStart", item.timeStart)
            putString("timeEnd", item.timeEnd)
            putString("description", item.description)
            putLong("parentId", item.parentId ?: -1)
            putStringArrayList("tags", ArrayList(item.tags))
            putLongArray("favoriteScheduleIds", listFavorite.toLongArray())
        }

        val detailsFragment = CurrentEventFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.layout_fragment, detailsFragment)
            .addToBackStack(null)
            .commit()
    }
}