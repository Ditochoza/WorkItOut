package es.ucm.fdi.workitout.viewModel

import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.ValidationResult
import es.ucm.fdi.workitout.utils.ValidationExerciseUtil

class ViewExerciseViewModel: ViewModel() {

    fun addVideoToExercise(videoLink: String, tilVideoLink: TextInputLayout,
                      exercise: Exercise, sModel: MainSharedViewModel
    ) {
        var newVideoLink = videoLink
        if (videoLink.startsWith("https://youtu.be/"))
            newVideoLink = "https://www.youtube.com/watch?v=" + videoLink.substringAfter("https://youtu.be/")
        val result = ValidationExerciseUtil.validateVideo(
            newVideoLink to tilVideoLink,
            exercise
        )

        if (result is ValidationResult.Success)
            sModel.addVideoToExercise(newVideoLink, exercise)
        else if (result is ValidationResult.FailedToast)
            sModel.showToast(result.resMessage)
    }
}