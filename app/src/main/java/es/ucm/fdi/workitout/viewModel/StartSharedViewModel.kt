package es.ucm.fdi.workitout.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.model.ValidationResult
import es.ucm.fdi.workitout.repository.UserRepository
import es.ucm.fdi.workitout.utils.ValidationUserUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StartSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val userRepository = UserRepository()

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()


    private val _shortToastRes = MutableSharedFlow<Int>()
    val shortToastRes: SharedFlow<Int> = _shortToastRes.asSharedFlow()

    private val _navigateActionRes = MutableSharedFlow<Int>()
    val navigateActionRes: SharedFlow<Int> = _navigateActionRes.asSharedFlow()

    private val _login = MutableSharedFlow<String>()
    val login: SharedFlow<String> = _login.asSharedFlow()

    fun login(tilEmail: TextInputLayout, tilPassword: TextInputLayout) {
        val result = ValidationUserUtil.validateLogin(
            user.value.email to tilEmail,
            user.value.tempPassword to tilPassword
        )

        if (result is ValidationResult.Success) {
            viewModelScope.launch {
                _loading.emit(true)
                val resultUser = userRepository.login(user.value.email, user.value.tempPassword)
                _loading.emit(false)

                if (resultUser is DatabaseResult.Success) {
                    resultUser.data?.email?.let { _login.emit(it) }
                } else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
            }
        }
    }

    fun register(tilName: TextInputLayout, tilEmail: TextInputLayout, tilPassword: TextInputLayout,
                 tilPasswordValidate: TextInputLayout) {
        val result = ValidationUserUtil.validateRegister(
            user.value.name to tilName,
            user.value.email to tilEmail,
            user.value.tempPassword to tilPassword,
            user.value.tempPasswordValidate to tilPasswordValidate
        )

        if (result is ValidationResult.Success) {
            viewModelScope.launch {
                _loading.emit(true)
                val resultUser = userRepository.register(user.value.name, user.value.email, user.value.tempPassword)
                _loading.emit(false)

                if (resultUser is DatabaseResult.Success) {
                    resultUser.data?.email?.let { _login.emit(it) }
                } else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
            }
        }
    }

    //Emite una acción de navegación como Resource
    fun navigate(navActionRes: Int) {
        viewModelScope.launch { _navigateActionRes.emit(navActionRes) }
    }

    fun clearErrors(til: TextInputLayout) {
        til.error = ""
    }

    fun saveStateHandle() {
        savedStateHandle.set(::user.name, user.value)
    }
}