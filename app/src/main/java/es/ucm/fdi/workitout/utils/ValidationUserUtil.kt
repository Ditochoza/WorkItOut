package es.ucm.fdi.workitout.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.ValidationResult

object ValidationUserUtil {

    fun validateLogin(
        email: Pair<String, TextInputLayout>,
        tempPassword: Pair<String, TextInputLayout>
    ) : ValidationResult {
        var result: ValidationResult = ValidationResult.success()

        if (email.second.tilError( /** Error si el email está vacío */
                email.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (email.second.tilError( /** Error si el formato de email no es válido */
                !Patterns.EMAIL_ADDRESS.matcher(email.first).matches(),
                resError = R.string.invalid_email
            )
        ) result = ValidationResult.failed()

        if (tempPassword.second.tilError( /** Error si la contraseña está vacía */
                tempPassword.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (tempPassword.second.tilError( /** Error si la contraseña tiene menos de 8 caracteres */
                (tempPassword.first.length < 8),
                resError = R.string.short_password
            )
        ) result = ValidationResult.failed()

        return result
    }

    fun validateRegister(
        name: Pair<String, TextInputLayout>,
        email: Pair<String, TextInputLayout>,
        tempPassword: Pair<String, TextInputLayout>,
        tempPasswordValidate: Pair<String, TextInputLayout>,
    ) : ValidationResult {
        var result: ValidationResult = ValidationResult.success()

        if (name.second.tilError( /** Error si el nombre está vacío */
                name.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()

        if (email.second.tilError( /** Error si el email está vacío */
                email.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (email.second.tilError( /** Error si el formato de email no es válido */
                !Patterns.EMAIL_ADDRESS.matcher(email.first).matches(),
                resError = R.string.invalid_email
            )
        ) result = ValidationResult.failed()

        if (tempPasswordValidate.second.tilError( /** Error si las contraseñas no coinciden */
                (tempPassword.first.isNotEmpty() && tempPasswordValidate.first.isNotEmpty() &&
                        tempPassword.first != tempPasswordValidate.first),
                resError = R.string.passwords_not_equal
            )
        ) result = ValidationResult.failed()

        if (tempPassword.second.tilError( /** Error si la contraseña está vacía */
                tempPassword.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (tempPassword.second.tilError( /** Error si la contraseña tiene menos de 8 caracteres */
                (tempPassword.first.length < 8),
                resError = R.string.short_password
            )
        ) result = ValidationResult.failed()

        if (tempPasswordValidate.second.tilError( /** Error si la contraseña está vacía */
                tempPasswordValidate.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (tempPasswordValidate.second.tilError( /** Error si la contraseña tiene menos de 8 caracteres */
                (tempPasswordValidate.first.length < 8),
                resError = R.string.short_password
            )
        ) result = ValidationResult.failed()

        return result
    }
}