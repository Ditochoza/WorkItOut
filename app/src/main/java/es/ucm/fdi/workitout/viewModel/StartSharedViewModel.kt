package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.repository.UserRepository
import es.ucm.fdi.workitout.utils.DatabaseResult
import es.ucm.fdi.workitout.utils.ValidationUserUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StartSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()


    private val _shortToastRes = MutableSharedFlow<Int>()
    val shortToastRes: SharedFlow<Int> = _shortToastRes.asSharedFlow()

    private val _navigateActionRes = MutableSharedFlow<Int>()
    val navigateActionRes: SharedFlow<Int> = _navigateActionRes.asSharedFlow()

    private val _login = MutableSharedFlow<String>()
    val login: SharedFlow<String> = _login.asSharedFlow()

    private val userRepository = UserRepository()

    fun login(pb: ProgressBar, tilEmail: TextInputLayout, tilPassword: TextInputLayout) {
        val error = ValidationUserUtil.validateLogin(
            user.value.email to tilEmail,
            user.value.tempPassword to tilPassword
        )

        if (!error) {
            viewModelScope.launch {
                pb.visibility = View.VISIBLE
                val resultUser = userRepository.login(user.value.email, user.value.tempPassword)
                pb.visibility = View.GONE

                if (resultUser is DatabaseResult.Success) {
                    resultUser.data?.email?.let { _login.emit(it) }
                } else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
            }
        }
    }

    fun register(pb: ProgressBar, tilName: TextInputLayout, tilEmail: TextInputLayout, tilPassword: TextInputLayout,
                 tilPasswordValidate: TextInputLayout) {
        val error = ValidationUserUtil.validateRegister(
            user.value.name to tilName,
            user.value.email to tilEmail,
            user.value.tempPassword to tilPassword,
            user.value.tempPasswordValidate to tilPasswordValidate
        )

        if (!error) {
            viewModelScope.launch {
                pb.visibility = View.VISIBLE
                val resultUser = userRepository.register(user.value.name, user.value.email, user.value.tempPassword)
                pb.visibility = View.GONE

                if (resultUser is DatabaseResult.Success) {
                    resultUser.data?.email?.let { _login.emit(it) }
                } else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
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