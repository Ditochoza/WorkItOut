package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentCreateRoutineBinding
import es.ucm.fdi.workitout.viewModel.EditSharedViewModel
import java.time.DayOfWeek

class CreateRoutineFragment : Fragment() {
    private val editSharedViewModel: EditSharedViewModel by activityViewModels()

    private var _binding: FragmentCreateRoutineBinding? = null
    private val binding get() = _binding!!
    private val days = ArrayList<DayOfWeek>()

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

    fun setDaysOfWeek(view: View){
        if (view is CheckBox) {

            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_lunes -> {
                    if (checked) {
                        days.add(DayOfWeek.MONDAY)
                    }
                }
                R.id.checkbox_martes -> {
                    if (checked) {
                        days.add(DayOfWeek.TUESDAY)
                    }
                }
                R.id.checkbox_miercoles -> {
                    if (checked) {
                        days.add(DayOfWeek.WEDNESDAY)
                    }
                }
                R.id.checkbox_jueves-> {
                    if (checked) {
                        days.add(DayOfWeek.THURSDAY)
                    }
                }
                R.id.checkbox_viernes -> {
                    if (checked) {
                        days.add(DayOfWeek.FRIDAY)
                    }
                }
                R.id.checkbox_sabado -> {
                    if (checked) {
                        days.add(DayOfWeek.SATURDAY)
                    }
                }
                R.id.checkbox_domingo -> {
                    if (checked) {
                        days.add(DayOfWeek.SUNDAY)
                    }
                }
            }
            editSharedViewModel.routine.value.days = this.days
        }
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
            editSharedViewModel.routine.value.hour = picker.hour.toString()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}