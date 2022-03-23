package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import es.ucm.fdi.workitout.model.DatabaseResult
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

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()


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

    fun saveStateHandle() {
        savedStateHandle.set(::user.name, user.value)
    }
}