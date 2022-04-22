package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentCreateRoutineBinding
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.createEditTextTimePicker
import es.ucm.fdi.workitout.utils.withTime
import es.ucm.fdi.workitout.utils.zeroTimestamp
import es.ucm.fdi.workitout.viewModel.CreateRoutineViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class CreateRoutineFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: CreateRoutineViewModel by activityViewModels()

    private var _binding: FragmentCreateRoutineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateRoutineBinding.inflate(inflater, container, false)

        if(mainSharedViewModel.selectedRoutine.value.id.isNotEmpty()){
            viewModel.editRoutine(mainSharedViewModel.selectedRoutine.value)
        }

        binding.sModel = mainSharedViewModel
        binding.vModel = viewModel
        binding.loading = mainSharedViewModel.loading.value
        binding.tempImageUri = mainSharedViewModel.tempImageUri.value
        binding.mainActivity = activity as MainActivity?
        binding.lifecycleOwner = viewLifecycleOwner

        setupTimePicker()
        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.tempImageUri.collectLatestFlow(this) { binding.tempImageUri = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    private fun setupTimePicker() {
        activity?.createEditTextTimePicker(binding.etTime, binding.tilTime,
            "", R.string.time_scheduled) { viewModel.updateTime(zeroTimestamp().withTime(it)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        viewModel.saveStateHandle()
        super.onPause()
    }
}