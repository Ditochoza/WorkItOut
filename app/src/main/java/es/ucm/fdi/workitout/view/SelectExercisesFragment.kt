package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.DialogSelectSetsRepsExerciseRoutineBinding
import es.ucm.fdi.workitout.databinding.FragmentSelectExercisesBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.ExerciseSetsReps
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.createAlertDialog
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class SelectExercisesFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentSelectExercisesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSelectExercisesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.emptyExercise = Exercise()
        binding.user = mainSharedViewModel.user.value
        binding.exercises = mainSharedViewModel.exercises.value
        binding.routine = mainSharedViewModel.selectedRoutine.value
        binding.loading = mainSharedViewModel.loading.value
        binding.activity = activity as MainActivity?
        binding.fragment = this

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.user.collectLatestFlow(this) { binding.user = it }
        mainSharedViewModel.exercises.collectLatestFlow(this) { binding.exercises = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
        mainSharedViewModel.selectedRoutine.collectLatestFlow(this) { binding.routine = it }
    }

    fun selectExercise(exercise: Exercise, routine: Routine, selected: Boolean): Boolean {
        val newExercises = routine.exercises.toMutableList()
        val newExercisesSetsReps = routine.exercisesSetsReps.toMutableList()
        if (selected) {
            newExercises.removeIf { it.id == exercise.id }
            newExercisesSetsReps.removeIf { it.idExercise.contains(exercise.id) }
            mainSharedViewModel.setAndNavigate(routine.copy(exercises = newExercises, exercisesSetsReps = newExercisesSetsReps))
        } else {
            activity?.let { activity ->
                val binding = DialogSelectSetsRepsExerciseRoutineBinding.inflate(activity.layoutInflater)
                binding.npSets.minValue = 1
                binding.npSets.maxValue = 30
                if (exercise.useReps) {
                    binding.llReps.visibility = View.VISIBLE
                    binding.npReps.minValue = 1
                    binding.npReps.maxValue = 30
                    binding.npReps.value = 1
                }
                activity.createAlertDialog(exercise.name,
                    ok = R.string.add to {
                        val idExercise =
                            if (exercise.idUser == mainSharedViewModel.user.value.email) //Si es un ejercicio creado por el usuario
                                "${DbConstants.COLLECTION_USERS}/${exercise.idUser}/${DbConstants.USER_COLLECTION_EXERCISES}/${exercise.id}"
                            else //Si es un ejercicio de la app
                                "${DbConstants.COLLECTION_EXERCISES}/${exercise.id}"

                        newExercisesSetsReps.add(ExerciseSetsReps(
                            idExercise, binding.npSets.value, binding.npReps.value
                        ))
                        newExercises.add(exercise.copy(
                            tempExerciseRoutineSets = binding.npSets.value,
                            tempExerciseRoutineReps = binding.npReps.value
                        ))

                        mainSharedViewModel.setAndNavigate(routine.copy(
                            exercises = newExercises,
                            exercisesSetsReps = newExercisesSetsReps)
                        )
                    },
                    cancel = R.string.cancel to {}
                ).setView(binding.root).create().show()
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


