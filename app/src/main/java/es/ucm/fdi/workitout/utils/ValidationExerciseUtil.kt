package es.ucm.fdi.workitout.utils

import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.ValidationResult

object ValidationExerciseUtil {

    fun validateExercise(
        image: Pair<ImageView, TextView>,
        name: Pair<String, TextInputLayout>,
        description: Pair<String, TextInputLayout>,
        muscles: Pair<List<String>,TextView>
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


        return result
    }

    fun validateVideoLink(videoLink: Pair<String,TextInputLayout>): Boolean{
        var error: Boolean = false

        if(videoLink.first.isNotEmpty() && !videoLink.first.contains("https://www.youtube.com/watch?v=", ignoreCase = true)){
            error = true
            videoLink.second.error = "video url is malformed"
        }

        return error
    }

}