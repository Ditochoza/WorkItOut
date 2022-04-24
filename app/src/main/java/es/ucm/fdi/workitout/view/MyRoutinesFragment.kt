package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentMyRoutinesBinding
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.createAlertDialog
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class MyRoutinesFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentMyRoutinesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyRoutinesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.user = mainSharedViewModel.user.value
        binding.emptyRoutine = Routine()
        binding.fragment = this
        binding.activity = activity as MainActivity?
        binding.loading = mainSharedViewModel.loading.value
        binding.lifecycleOwner = viewLifecycleOwner

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.user.collectLatestFlow(this) { binding.user = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    fun onLongClickRoutine (routine: Routine):Boolean {
        activity?.let { activity ->
            MaterialAlertDialogBuilder(activity)
                .setItems(R.array.array_options_exercise_routine) { _, i ->
                    when (i) {
                        0 -> { //Editar  rutina
                            mainSharedViewModel.setAndNavigate(routine,
                                R.id.action_myRoutinesFragment_to_createRoutineFragment)
                        }
                        1 -> { //Eliminar rutina
                            context?.createAlertDialog(getString(R.string.delete_routine, routine.name),
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