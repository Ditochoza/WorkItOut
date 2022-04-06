package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.repository.RoutineRepository
import es.ucm.fdi.workitout.view.EditActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditSharedViewModel(application: Application,
                          private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _routine = MutableStateFlow(savedStateHandle.get(::routine.name) ?: Routine())
    val routine: StateFlow<Routine> = _routine.asStateFlow()

    private val routineRepository = RoutineRepository()

    fun createRoutine() {
        viewModelScope.launch{
            val resultRoutine = routineRepository.addRoutine(
                Routine(name = routine.value.name,description = routine.value.description,hour = routine.value.hour,
                days = routine.value.days))
        }
    }

    fun clearErrors(til: TextInputLayout) {
        til.error = ""
    }

    fun saveStateHandle() {
        savedStateHandle.set(::routine.name, routine.value)
    }
}