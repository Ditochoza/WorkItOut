package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.ExerciseValidationUtil
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.UserValidationUtil
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.view.EditActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URI

class EditSharedViewModel(application: Application,
                          private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _exercise = MutableStateFlow(savedStateHandle.get(::exercise.name) ?: Exercise())
    val exercise: StateFlow<Exercise> = _exercise.asStateFlow()

    private val _tempImageUri = MutableStateFlow(savedStateHandle.get(::tempImageUri.name) ?: Uri.EMPTY)
    val tempImageUri: StateFlow<Uri> = _tempImageUri.asStateFlow()

    var muscles: List<String>  = listOf("biceps","triceps","abdominales", "gemelos","espalda","cuello","cabeza","piernas")

    fun createExercise(imageTexView: TextView,imageTexViewError: TextView,
                       tilName: TextInputLayout, tilDescription: TextInputLayout,
                       musclesTexView: TextView) {

        Log.i("CreateExerciseFragment","muscles list: $exercise.value.muscles")

        val error = ExerciseValidationUtil.validateExercise(
            exercise.value.image to (imageTexView to imageTexViewError),
            exercise.value.name to tilName,
            exercise.value.description to tilDescription,
            exercise.value.muscles to musclesTexView
        )

        if(!error){
            Log.i("CreateExerciseFragment","Objeto: $exercise.value")
            viewModelScope.launch(Dispatchers.IO) {
               //registar ejercicio en base de datos
            }
        }
    }


    fun clearErrors(til: TextInputLayout) {
        til.error = ""
    }

    fun setMusclesList(musclesList: List<String>){
        _exercise.value.muscles = musclesList
    }

    fun setTempImage(uri: Uri){
        _tempImageUri.value = uri
        exercise.value.image = uri
    }

    fun saveStateHandle() {
        Log.d("Error", exercise.value.toString())
        savedStateHandle.set(::exercise.name, exercise.value)
    }
}