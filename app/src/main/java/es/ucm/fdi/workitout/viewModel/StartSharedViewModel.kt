package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import es.ucm.fdi.workitout.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StartSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()

    fun getLoginCredentials(email: String, password: String) {
        Log.d("ViewModel", "1$email $password")
        Log.d("ViewModel", "2${user.value.email} ${user.value.tempPassword}")
    }

    fun register(name: String, email: String, password: String, passwordValidate: String) {

    }

    fun saveStateHandle() {
        savedStateHandle.set(::user.name, user.value)
    }
}