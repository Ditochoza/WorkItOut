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

        when {
            tempPasswordValidate.second.tilError( /** Error si la contraseña está vacía */
                tempPasswordValidate.first.isEmpty(),
                resError = R.string.field_not_empty
            ) -> result = ValidationResult.failed()
            tempPasswordValidate.second.tilError( /** Error si la contraseña tiene menos de 8 caracteres */
                (tempPasswordValidate.first.length < 8),
                resError = R.string.short_password
            ) -> result = ValidationResult.failed()
            tempPasswordValidate.second.tilError(  /** Error si la contraseña nueva es diferente que la de validación */
                (tempPassword.first.isNotEmpty() && tempPasswordValidate.first.isNotEmpty() &&
                        tempPassword.first != tempPasswordValidate.first),
                resError = R.string.passwords_not_equal
            ) -> result = ValidationResult.failed()
        }

        return result
    }

    fun validateChangePassword(
        currentPassword: Pair<String, TextInputLayout>,
        newPassword: Pair<String, TextInputLayout>,
        validationPassword: Pair<String, TextInputLayout>,
    ) : ValidationResult {
        var result: ValidationResult = ValidationResult.success()

        if (currentPassword.second.tilError( /** Error si la contraseña actual está vacío */
                currentPassword.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (currentPassword.second.tilError( /** Error si la contraseña actual tiene menos de 8 caracteres */
                currentPassword.first.length < 8,
                resError = R.string.short_password
            )
        ) result = ValidationResult.failed()

        when {
            newPassword.second.tilError( /** Error si la nueva contraseña está vacío */
                newPassword.first.isEmpty(),
                resError = R.string.field_not_empty
            ) -> result = ValidationResult.failed()
            newPassword.second.tilError( /** Error si la contraseña nueva tiene menos de 8 caracteres */
                newPassword.first.length < 8,
                resError = R.string.short_password
            ) -> result = ValidationResult.failed()
            newPassword.second.tilError( /** Error si la contraseña nueva es igual que la actual */
                newPassword.first == currentPassword.first,
                resError = R.string.new_password_equal_current_password
            ) -> result = ValidationResult.failed()
        }

        when {
            validationPassword.second.tilError( /** Error si la contraseña de validación está vacío */
                validationPassword.first.isEmpty(),
                resError = R.string.field_not_empty
            ) -> result = ValidationResult.failed()
            validationPassword.second.tilError( /** Error si la contraseña de validación tiene menos de 8 caracteres */
                validationPassword.first.length < 8,
                resError = R.string.short_password
            ) -> result = ValidationResult.failed()
            validationPassword.second.tilError( /** Error si la contraseña nueva es diferente que la de validación */
                newPassword.first != validationPassword.first,
                resError = R.string.passwords_not_equal
            ) -> result = ValidationResult.failed()
        }

        return result
    }

    fun validateDeleteAccount(
        currentPassword: Pair<String, TextInputLayout>
    ) : ValidationResult {
        var result: ValidationResult = ValidationResult.success()

        if (currentPassword.second.tilError( /** Error si la contraseña actual está vacío */
                currentPassword.first.isEmpty(),
                resError = R.string.field_not_empty
            )
        ) result = ValidationResult.failed()
        else if (currentPassword.second.tilError( /** Error si la contraseña actual tiene menos de 8 caracteres */
                currentPassword.first.length < 8,
                resError = R.string.short_password
            )
        ) result = ValidationResult.failed()

        return result
    }
}