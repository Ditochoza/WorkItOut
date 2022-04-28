package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.databinding.FragmentTrainingExercisesBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class TrainingExercisesFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentTrainingExercisesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrainingExercisesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routine = mainSharedViewModel.selectedRoutine.value
        binding.loading = mainSharedViewModel.loading.value
        binding.activity = activity as MainActivity?
        binding.emptyExercises = emptyList<Exercise>()

        setupCollectors()

        return binding.root
    }


    private fun setupCollectors() {
        mainSharedViewModel.selectedRoutine.collectLatestFlow(this) { binding.routine = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


