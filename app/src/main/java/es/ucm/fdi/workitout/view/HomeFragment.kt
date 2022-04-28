package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentHomeBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.*
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class HomeFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Exercise().muscles.joinToString(", ")

        binding.sModel = mainSharedViewModel
        binding.loading = mainSharedViewModel.loading.value
        binding.user = mainSharedViewModel.user.value
        binding.activity = activity as MainActivity?
        binding.emptyList = emptyList<Exercise>()
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
        mainSharedViewModel.user.collectLatestFlow(this) { binding.user = it }
    }

    fun startTraining(routine: Routine) {
        createTrainingNotification(routine)
        mainSharedViewModel.setAndNavigate(
            routine.copy(exercises = routine.getExercisesWithNewRecords()),
            R.id.action_homeFragment_to_trainingExercisesFragment
        )
    }

    private fun createTrainingNotification(routine: Routine) {
        context?.deleteNotification(routine.requestRoutineIdNotification)

        context?.createTrainingNotification(
            requestCode = routine.requestRoutineIdNotification,
            title = getString(R.string.training_in_progress),
            message = getString(R.string.routine_routine, routine.name)
        )
    }

    fun onLongClickRoutine (routine: Routine):Boolean {
        activity?.let { activity ->
            MaterialAlertDialogBuilder(activity)
                .setItems(R.array.array_options_exercise_routine) { _, i ->
                    when (i) {
                        0 -> { //Editar  rutina
                            mainSharedViewModel.setAndNavigate(routine,
                                R.id.action_homeFragment_to_createRoutineFragment)
                        }
                        1 -> { //Eliminar rutina
                            context?.createAlertDialog(getString(
                                R.string.delete_routine,
                                routine.name
                            ),
                                message = R.string.delete_routine_confirmation_message,
                                icon = R.drawable.ic_round_delete_outline_24,
                                ok = R.string.confirm to { mainSharedViewModel.deleteRoutine(routine) },
                                cancel = R.string.cancel to {}
                            )?.show()
                        }
                    }
                }
                .setTitle(routine.name).show()
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}