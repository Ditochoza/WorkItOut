package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.view.EditActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditSharedViewModel(application: Application,
                          private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _exercise = MutableStateFlow(savedStateHandle.get(::exercise.name) ?: Exercise())
    val exercise: StateFlow<Exercise> = _exercise.asStateFlow()
    var selectedMuscles = mutableListOf<String>()

    fun clearErrors(til: TextInputLayout) {
        til.error = ""
    }

    fun saveStateHandle() {
        savedStateHandle.set(::exercise.name, exercise.value)
    }
}