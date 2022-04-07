package es.ucm.fdi.workitout.utils

import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.ValidationResult

object ValidationRoutineUtil {

    fun validateRoutine(
        image: Pair<ImageView, TextView>,
        name: Pair<String, TextInputLayout>,
        description: Pair<String, TextInputLayout>,
        time: Pair<Timestamp, TextInputLayout>,
        weekDay: Pair<Int, TextInputLayout>,
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

        if (time.second.tilError( /** Error si hay día de la semana pero no hora */
                time.first == zeroTimestamp() && weekDay.first != -1,
                resError = R.string.unschedule_or_complete_field
        )) result = ValidationResult.failed()

        if (weekDay.second.tilError( /** Error si hay hora pero no día de la semana */
                time.first != zeroTimestamp() && weekDay.first == -1,
                resError = R.string.unschedule_or_complete_field
        )) result = ValidationResult.failed()

        return result
    }
}