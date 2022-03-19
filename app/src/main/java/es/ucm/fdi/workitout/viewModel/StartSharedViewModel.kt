package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.UserValidationUtil
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StartSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val userRepository = UserRepository()

    fun getLoginCredentials(email: String, password: String) {
        Log.d("ViewModel", "1$email $password")
        Log.d("ViewModel", "2${user.value.email} ${user.value.tempPassword}")
    }

    fun login(tilEmail: TextInputLayout, tilPassword: TextInputLayout) {
        val error = UserValidationUtil.validateLogin(
            user.value.email to tilEmail,
            user.value.tempPassword to tilPassword
        )

        if (!error) {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.login(user.value.email, user.value.tempPassword)
            }

        }
    }

    fun register(tilName: TextInputLayout, tilEmail: TextInputLayout, tilPassword: TextInputLayout,
                 tilPasswordValidate: TextInputLayout) {
        val error = UserValidationUtil.validateRegister(
            user.value.name to tilName,
            user.value.email to tilEmail,
            user.value.tempPassword to tilPassword,
            user.value.tempPasswordValidate to tilPasswordValidate
        )

        if(!error){
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.register(user.value.name, user.value.email, user.value.tempPassword)
            }
        }
    }

    fun clearErrors(til: TextInputLayout) {
        til.error = ""
    }

    fun saveStateHandle() {
        savedStateHandle.set(::user.name, user.value)
    }
}