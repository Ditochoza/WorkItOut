package es.ucm.fdi.workitout.viewModel

import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.ValidationResult
import es.ucm.fdi.workitout.utils.ValidationRoutineUtil
import es.ucm.fdi.workitout.utils.zeroTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateRoutineViewModel(private var savedStateHandle: SavedStateHandle): ViewModel() {

    private val _tempRoutine = MutableStateFlow(savedStateHandle.get(::tempRoutine.name) ?: Routine())
    val tempRoutine: StateFlow<Routine> = _tempRoutine.asStateFlow()

    fun createRoutine(ivRoutine: ImageView, tvImageError: TextView, tilName: TextInputLayout,
                      tilDescription: TextInputLayout, tilTime: TextInputLayout,
                      tilWeekDay: TextInputLayout, sModel: MainSharedViewModel
    ) {
        val result = ValidationRoutineUtil.validateRoutine(
            ivRoutine to tvImageError,
            tempRoutine.value.name to tilName,
            tempRoutine.value.description to tilDescription,
            tempRoutine.value.timeTimestampScheduled to tilTime,
            tempRoutine.value.dayOfWeekScheduled to tilWeekDay
        )

        if(result is ValidationResult.Success){
            sModel.createRoutine(ivRoutine, tempRoutine.value)
        }
    }

    fun deSchedule(etTime: EditText, tilTime: TextInputLayout, etWeekDay: AutoCompleteTextView, tilWeekDay: TextInputLayout) {
        etWeekDay.text.clear()
        tilTime.error = ""
        _tempRoutine.value.dayOfWeekScheduled = -1
        etTime.text.clear()
        tilWeekDay.error = ""
        _tempRoutine.value.timeTimestampScheduled = zeroTimestamp()
    }

    fun updateWeekDay(dayOfWeekIndex: Int) {
        _tempRoutine.value.dayOfWeekScheduled = dayOfWeekIndex
        savedStateHandle.set(::tempRoutine.name, tempRoutine.value)
    }

    fun updateTime(time: Timestamp) {
        _tempRoutine.value.timeTimestampScheduled = time
        savedStateHandle.set(::tempRoutine.name, tempRoutine.value)
    }

    fun editRoutine(routine: Routine){
        _tempRoutine.value = routine
    }

    fun saveStateHandle() {
        savedStateHandle.set(::tempRoutine.name, tempRoutine.value)
    }
}
