package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.repository.DbConstants
import es.ucm.fdi.workitout.repository.UserDataStore
import es.ucm.fdi.workitout.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application)  {
    private val userRepository = UserRepository()
    private val userDataStore by lazy { UserDataStore(application) }

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User("ditochoza@gmail.com", "Víctor"))
    val user: StateFlow<User> = _user.asStateFlow()

    //TODO Poner a empty al lanzar el fragment para un nuevo ejercicio
    private val _tempImageUri = MutableStateFlow(savedStateHandle.get(::tempImageUri.name) ?: Uri.EMPTY)
    val tempImageUri: StateFlow<Uri> = _tempImageUri.asStateFlow()


    //private val _navigateActionRes = MutableSharedFlow<Int>()
    //val navigateActionRes: SharedFlow<Int> = _navigateActionRes.asSharedFlow()

    private val _shortToastRes = MutableSharedFlow<Int>()
    val shortToastRes: SharedFlow<Int> = _shortToastRes.asSharedFlow()

    //private val _logout = MutableSharedFlow<Boolean>()
    //val logout: SharedFlow<Boolean> = _logout.asSharedFlow()


    //Obtiene un valor del usuario de DataStore instantáneamente
    suspend fun getStringUserDataStore(key: String): String {
        return userDataStore.getStringUserDataStoreOrEmpty(key)
    }

    fun fetchAll(email: String = user.value.email) {
        viewModelScope.launch {
            listOf(
                async { fetchUserByEmail(email) }
            ).awaitAll()
        }
    }

    private suspend fun fetchUserByEmail(email: String) {
        if (email.isNotEmpty()) { //Si el usuario no es invitado
            val resultUser = userRepository.fetchUserByEmail(email)

            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = user.value.copy(email = newUser.email, name = newUser.name)

                Log.d("ASD3", user.value.name)
                //Actualizamos el StateFlow de usuario y los valores del usuario en DataStore
                savedStateHandle.set(::user.name, user.value)
                userDataStore.putString(DbConstants.USER_EMAIL, newUser.email)
            }
            else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    fun createRoutine(ivRoutine: ImageView, newRoutine: Routine) {
        viewModelScope.launch {
            val resultUser = userRepository.uploadRoutineAndImage(user.value.email, newRoutine,
                ivRoutine, tempImageUri.value != Uri.EMPTY)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
            }
            else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    fun clearErrors(til: TextInputLayout) { til.error = "" }

    fun setTempImage(uri: Uri) {
        _tempImageUri.value = uri
    }

    fun saveStateHandle() {
        savedStateHandle.set(::user.name, user.value)
    }
}