package es.ucm.fdi.workitout.viewModel

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.ValidationResult
import es.ucm.fdi.workitout.utils.ValidationExerciseUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateExerciseViewModel(private var savedStateHandle: SavedStateHandle): ViewModel() {

    private val _tempExercise = MutableStateFlow(savedStateHandle.get(::tempExercise.name) ?: Exercise())
    val tempExercise: StateFlow<Exercise> = _tempExercise.asStateFlow()

    fun createExercise(ivExercise: ImageView, tvImageError: TextView, tilName: TextInputLayout,
                       tilDescription: TextInputLayout, tvMusclesError: TextView,
                       tvMeasureByError: TextView, sModel: MainSharedViewModel
    ) {
        val result = ValidationExerciseUtil.validateExercise(
            ivExercise to tvImageError,
            tempExercise.value.name to tilName,
            tempExercise.value.description to tilDescription,
            tempExercise.value.muscles to tvMusclesError,
            arrayListOf(tempExercise.value.measureByReps, tempExercise.value.measureByTime,
                tempExercise.value.measureByWeight) to tvMeasureByError
        )

        if(result is ValidationResult.Success){
            sModel.createExercise(ivExercise, tempExercise.value)
        }
    }

    fun editExercise(exercise: Exercise){
        _tempExercise.value = exercise
    }

    fun updateMuscles(muscles: List<String>) {
        _tempExercise.value.muscles = muscles
    }

    fun saveStateHandle() {
        savedStateHandle.set(::tempExercise.name, tempExercise.value)
    }


}