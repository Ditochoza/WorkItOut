package es.ucm.fdi.workitout.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object UserValidationUtil {

    fun validateLogin(
        email: Pair<String, TextInputLayout>,
        tempPassword: Pair<String, TextInputLayout>
    ) : Boolean {
        var error = false

        if (email.first.isEmpty()) {
            error = true
            email.second.error = "Debes introducir tu correo electronico"
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email.first).matches()){
            email.second.error = "Debes introducir una dirrección de correo válida"
        }


        if (tempPassword.first.isEmpty()) {
            error = true
            tempPassword.second.error = "Debes introducir tu contraseña"
        } else if (tempPassword.first.length < 8) {
            error = true
            tempPassword.second.error = "La contraseña debe ser de al menos 8 caracteres"
        }

        return error
    }

    fun validateRegister(
        name: Pair<String, TextInputLayout>,
        email: Pair<String, TextInputLayout>,
        tempPassword: Pair<String, TextInputLayout>,
        tempPasswordValidate: Pair<String, TextInputLayout>,
    ) : Boolean {
        var error = false

        if (name.first.isEmpty()) {
            error = true
            name.second.error = "Debes introducir tu nombre"
        }

        if (email.first.isEmpty()) {
            error = true
            email.second.error = "Debes introducir tu correo electronico"
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email.first).matches()){
            email.second.error = "Debes introducir una dirrección de correo válida"
        }


        if (tempPassword.first.isEmpty()) {
            error = true
            tempPassword.second.error = "Debes introducir tu contraseña"
        } else if (tempPassword.first.length < 8) {
            error = true
            tempPassword.second.error = "La contraseña debe ser de al menos 8 caracteres"
        }

        if (tempPasswordValidate.first.isEmpty()) {
            error = true
            tempPasswordValidate.second.error  = "Debes volver a introducir tu contraseña"
        } else if (tempPasswordValidate.first.length < 8) {
            error = true
            tempPasswordValidate.second.error = "La contraseña debe ser de al menos 8 caracteres"
        }

        if(tempPassword.first.isNotEmpty() && tempPasswordValidate.first.isNotEmpty() &&
            tempPassword.first != tempPasswordValidate.first){
            error = true
            tempPasswordValidate.second.error = "Las contaseñas deben coincidir"
        }

        return error
    }
}