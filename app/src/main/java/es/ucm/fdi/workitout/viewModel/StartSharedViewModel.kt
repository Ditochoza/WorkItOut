package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StartSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    fun getLoginCredentials(email: String, password: String) {
        Log.d("ViewModel", "1$email $password")
        Log.d("ViewModel", "2${user.value.email} ${user.value.tempPassword}")
    }

    fun register(name: String, email: String, password: String, passwordValidate: String) {

        var error = false;
        //Comprobar que los valores introducidos por el usuario no son vacios
        if (name.isEmpty()){
            error = true;
        }

        if (email.isEmpty()){
            error = true;
        }

        if (password.isEmpty()){
            error = true;
        }

        if (passwordValidate.isEmpty()){
            error = true;
        }

        //Comprobar contraseña y guardar en la base de datos
        if (!error  && password == passwordValidate){
            db.collection("users").document(email).set(
                hashMapOf("Name" to name,
                    "password" to password)
            )
        }
    }

    fun saveStateHandle() {
        savedStateHandle.set(::user.name, user.value)
    }
}