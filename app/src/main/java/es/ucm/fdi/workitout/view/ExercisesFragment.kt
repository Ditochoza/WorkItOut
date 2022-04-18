package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentExercisesBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.createAlertDialog
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class ExercisesFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.user = mainSharedViewModel.user.value
        binding.exercises = mainSharedViewModel.exercises.value
        binding.loading = mainSharedViewModel.loading.value
        binding.fragment = this

        setupCollectors()

        return binding.root
    }

    fun onLongClickExercise (exercise: Exercise):Boolean {
        if (exercise.idUser == mainSharedViewModel.user.value.email) {
            activity?.let { activity ->
                MaterialAlertDialogBuilder(activity)
                    .setItems(R.array.array_options_exercise) { _, i ->
                        when (i) {
                            0 -> { //Editar  ejercicio
                                mainSharedViewModel.navigateAndSet(exercise,
                                    R.id.action_exercisesFragment_to_createExerciseFragment)
                            }
                            1 -> { //Eliminar ejercicio
                                context?.createAlertDialog(getString(R.string.delete_exercise, exercise.name),
                                    message = R.string.delete_exercise_confirmation_message,
                                    icon = R.drawable.ic_round_delete_outline_24,
                                    ok = R.string.confirm to { mainSharedViewModel.deleteExercise(exercise) },
                                    cancel = R.string.cancel to {}
                                )?.show()
                            }
                        }
                    }
                    .setTitle(exercise.name).show()
            }
            return true
        } else return false
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


