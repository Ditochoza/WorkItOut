package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.databinding.FragmentExercisesBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class ExercisesFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.emptyExercise = Exercise()
        binding.user = mainSharedViewModel.user.value
        binding.exercises = mainSharedViewModel.exercises.value
        binding.loading = mainSharedViewModel.loading.value
        binding.emptyRoutine = Routine()
        binding.activity = activity as MainActivity?

        setupCollectors()

        return binding.root
    }


    private fun setupCollectors() {
        mainSharedViewModel.user.collectLatestFlow(this) { binding.user = it }
        mainSharedViewModel.exercises.collectLatestFlow(this) { binding.exercises = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


