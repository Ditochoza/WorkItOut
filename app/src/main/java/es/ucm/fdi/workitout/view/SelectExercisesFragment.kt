package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.databinding.FragmentSelectExercisesBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.collectLatestFlow
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
        val newExercisesIds = routine.exercisesIds.toMutableList()
        if (selected) {
            newExercises.removeIf { it.id == exercise.id }
            newExercisesIds.removeIf { it.contains(exercise.id) }
        } else {
            newExercises.add(exercise)
            if (exercise.idUser == mainSharedViewModel.user.value.email) //Si es un ejercicio creado por el usuario
                newExercisesIds.add("${DbConstants.COLLECTION_USERS}/${exercise.idUser}/${DbConstants.USER_COLLECTION_EXERCISES}/${exercise.id}")
            else //Si es un ejercicio de la app
                newExercisesIds.add("${DbConstants.COLLECTION_EXERCISES}/${exercise.id}")
        }
        mainSharedViewModel.setAndNavigate(routine.copy(exercises = newExercises, exercisesIds = newExercisesIds))
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


