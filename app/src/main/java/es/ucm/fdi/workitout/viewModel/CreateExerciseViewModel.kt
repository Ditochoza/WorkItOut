package es.ucm.fdi.workitout.viewModel

import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.ValidationResult
import es.ucm.fdi.workitout.model.Video
import es.ucm.fdi.workitout.utils.ValidationExerciseUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateExerciseViewModel(private var savedStateHandle: SavedStateHandle): ViewModel() {

    private val _tempExercise = MutableStateFlow(savedStateHandle.get(::tempExercise.name) ?: Exercise())
    val tempExercise: StateFlow<Exercise> = _tempExercise.asStateFlow()

    private val _videoList = MutableStateFlow(emptyList<Video>())
    val videoList: StateFlow<List<Video>> = _videoList.asStateFlow()

    fun createExercise(ivExercise: ImageView, tvImageError: TextView, tilName: TextInputLayout,
                       tilDescription: TextInputLayout, tvMusclesError: TextView, sModel: MainSharedViewModel
    ) {
        val result = ValidationExerciseUtil.validateExercise(
            ivExercise to tvImageError,
            tempExercise.value.name to tilName,
            tempExercise.value.description to tilDescription,
            tempExercise.value.muscles to tvMusclesError
        )

        if(result is ValidationResult.Success){
            sModel.createExercise(ivExercise, tempExercise.value)
        }
    }

    fun onLongClickExerciseVideo(view: View, url:String) : Boolean{
        val applicationContext = view.context
        val builder = AlertDialog.Builder(applicationContext)
        with(builder)
        {
            setTitle("Manage Video")
            setNegativeButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                updateVideoLinks(url)
            })
            setPositiveButton("Cancel", null)
            show()
        }

        return true
    }

    fun updateVideoLinks(deletedVideo: String) {

        var newVideoData:List<Video> = _videoList.value.filter {
            it.url != deletedVideo
        }
        _videoList.value = newVideoData

        var newVideoList:List<String> = tempExercise.value.videoLinks.filter {
            it != deletedVideo
        }
        _tempExercise.value.videoLinks = newVideoList
    }


    fun updateMuscles(muscles: List<String>) {
        _tempExercise.value.muscles = muscles
    }

    fun saveStateHandle() {
        savedStateHandle.set(::tempExercise.name, tempExercise.value)
    }
}