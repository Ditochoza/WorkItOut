package es.ucm.fdi.workitout.viewModel

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.ValidationResult
import es.ucm.fdi.workitout.utils.ValidationUserUtil

class SettingsPreferencesViewModel: ViewModel() {
    fun validateAndUpdatePassword(
        currentPassword: String, tilCurrentPassword: TextInputLayout,
        newPassword: String, tilNewPassword: TextInputLayout,
        validationPassword: String, tilValidationPassword: TextInputLayout,
        sModel: MainSharedViewModel, alertDialog: AlertDialog
    ) {
        val result = ValidationUserUtil.validateChangePassword(
            currentPassword to tilCurrentPassword,
            newPassword to tilNewPassword,
            validationPassword to tilValidationPassword
        )

        if (result is ValidationResult.Success) {
            sModel.updatePassword(currentPassword, newPassword, alertDialog)
        }
    }

    fun validateAndDeleteAccount(
        currentPassword: String, tilCurrentPassword: TextInputLayout,
        sModel: MainSharedViewModel, alertDialog: AlertDialog
    ) {
        val result = ValidationUserUtil.validateDeleteAccount(
            currentPassword to tilCurrentPassword
        )

        if (result is ValidationResult.Success) {
            sModel.deleteAccount(currentPassword, alertDialog)
        }
    }

}