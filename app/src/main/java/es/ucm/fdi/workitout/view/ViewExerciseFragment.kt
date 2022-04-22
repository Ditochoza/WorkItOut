package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.databinding.FragmentViewExerciseBinding
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class ViewExerciseFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()


    private var _binding: FragmentViewExerciseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentViewExerciseBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.exercise = mainSharedViewModel.selectedExercise.value
        binding.lifecycleOwner = viewLifecycleOwner

        binding.videoList = mainSharedViewModel.videoList.value
        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.selectedExercise.collectLatestFlow(this) { binding.exercise = it }
        mainSharedViewModel.videoList.collectLatestFlow(this) { binding.videoList = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


