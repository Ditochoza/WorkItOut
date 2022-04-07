package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import es.ucm.fdi.workitout.databinding.FragmentCreateExerciseBinding
import es.ucm.fdi.workitout.utils.collectFlow
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class CreateExerciseFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: CreateExerciseViewModel by viewModels()

    private var _binding: FragmentCreateExerciseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateExerciseBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.vModel = viewModel
        binding.mainActivity = activity as MainActivity?
        binding.lifecycleOwner = viewLifecycleOwner

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.tempImageUri.collectLatestFlow(this) { binding.tempImageUri = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        mainSharedViewModel.saveStateHandle()
        viewModel.saveStateHandle()
        super.onPause()
    }
}


