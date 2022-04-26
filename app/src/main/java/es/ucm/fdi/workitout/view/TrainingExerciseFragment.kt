package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.databinding.FragmentTrainingExerciseBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.RecordLog
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.getExercisesWithNewRecords
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class TrainingExerciseFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentTrainingExerciseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrainingExerciseBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.loading = mainSharedViewModel.loading.value
        binding.routine = mainSharedViewModel.selectedRoutine.value
        binding.exercise = mainSharedViewModel.selectedRoutine.value.exercises
            .firstOrNull { it.id == mainSharedViewModel.selectedExercise.value.id } ?: Exercise()
        binding.fragment = this

        setupCollectors()

        return binding.root
    }


    private fun setupCollectors() {
        mainSharedViewModel.selectedExercise.collectLatestFlow(this) { binding.exercise = it }
        mainSharedViewModel.selectedRoutine.collectLatestFlow(this) { routine ->
            binding.routine = routine
            binding.exercise = routine.exercises
                .firstOrNull { it.id == mainSharedViewModel.selectedExercise.value.id } ?: Exercise()
        }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    fun saveRecord(reps: Int, weight: Int, time: Int, recordLog: RecordLog, exercise: Exercise, routine: Routine) {
        mainSharedViewModel.set(
            routine.copy(exercises = routine.getExercisesWithNewRecords(recordLog.pos, reps, weight, time, exercise)),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


