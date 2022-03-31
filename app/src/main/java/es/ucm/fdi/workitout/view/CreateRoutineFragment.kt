package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.viewModel.EditSharedViewModel

class CreateRoutineFragment : Fragment() {
    private val editSharedViewModel: EditSharedViewModel by activityViewModels()

    private var _binding: FragmentCreateRoutineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateRoutineBinding.inflate(inflater, container, false)

        binding.eModel = editSharedViewModel
        binding.routinesFrag = this
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    fun exercisesScreen(){
        view?.findNavController()?.navigate(R.id.action_createRoutineFragment_to_createExerciseFragment)
    }

    fun hourScreen(){
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Hour for Routine")
            .build()
        picker.show(childFragmentManager, "TAG")
        picker.addOnPositiveButtonClickListener {
            Log.d("HourPicker", "Hora: "+picker.hour)
        }
        picker.addOnNegativeButtonClickListener {
            Log.d("HourPicker", "Canceled")
        }
        picker.addOnCancelListener {
            Log.d("HourPicker", "Canceled")
        }
        picker.addOnDismissListener {
            Log.d("HourPicker", "Dismissed")
        }
    }
}