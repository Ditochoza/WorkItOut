package es.ucm.fdi.workitout.utils

import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.ValidationResult

object ValidationExerciseUtil {

    fun validateExercise(
        image: Pair<ImageView, TextView>,
        name: Pair<String, TextInputLayout>,
        description: Pair<String, TextInputLayout>,
        muscles: Pair<List<String>, TextView>,
        measureBy: Pair<ArrayList<Boolean>, TextView>
    ) : ValidationResult {
        var result: ValidationResult = ValidationResult.success()

        if (image.second.tvError( /** Error si no se ha seleccionado ninguna imagen de ejercicio */
                image.first.drawable == null
        )) result = ValidationResult.failed()

        when {
            name.second.tilError( /** Error si el nombre está vacío */
                name.first.isEmpty(),
                resError = R.string.field_not_empty
            ) -> result = ValidationResult.failed()
            name.second.tilError( /** Error si el nombre es muy largo */
                name.first.length > 35,
                resError = R.string.field_long
            ) -> result = ValidationResult.failed()
            name.second.tilError( /** Error si el nombre es muy corto */
                name.first.length < 5,
                resError = R.string.field_short
            ) -> result = ValidationResult.failed()
        }

        when {
            description.second.tilError( /** Error si la descripción está vacía */
                description.first.isEmpty(),
                resError = R.string.field_not_empty
            ) -> result = ValidationResult.failed()
            description.second.tilError( /** Error si la descripción es muy larga */
                description.first.length > 175,
                resError = R.string.field_long
            ) -> result = ValidationResult.failed()
            description.second.tilError( /** Error si la descripción es muy corta */
                description.first.length < 10,
                resError = R.string.field_short
            ) -> result = ValidationResult.failed()
        }

        if (muscles.second.tvError( /** Error si no se ha seleccionado ningún músculo */
                muscles.first.isEmpty()
        )) result = ValidationResult.failed()

        if (measureBy.second.tvError( /** Error si no se ha seleccionado ningún método para medir el ejercicio */
                (measureBy.first.count { it } == 0)
            )) result = ValidationResult.failed()

        return result
    }

    fun validateVideo(
        videoLink: Pair<String, TextInputLayout>,
        exercise: Exercise
    ): ValidationResult {
        var result: ValidationResult = ValidationResult.success()

        if (videoLink.second.tilError( /** Error si el link de video está vacío */
                videoLink.first.isEmpty(),
                resError = R.string.empty
        )) result = ValidationResult.failed()
        else if (videoLink.second.tilError( /** Error si el link del vídeo no contiene el https... */
                !videoLink.first.contains("https://www.youtube.com/watch?v=", ignoreCase = true),
                resError = R.string.start_by_youtube_link
        )) result = ValidationResult.failed()

        if (result is ValidationResult.Success)
            if (exercise.videoLinks.map { it.videoUrl }.contains(videoLink.first))
                return ValidationResult.failedToast(R.string.existing_video_link)

        return result
    }

}