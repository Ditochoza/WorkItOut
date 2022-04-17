package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.repository.UserDataStore
import es.ucm.fdi.workitout.repository.UserRepository
import es.ucm.fdi.workitout.utils.DbConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application)  {
    private val userRepository = UserRepository()
    private val userDataStore by lazy { UserDataStore(application) }

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()

    //TODO Poner a empty al lanzar el fragment para un nuevo ejercicio
    private val _tempImageUri = MutableStateFlow(savedStateHandle.get(::tempImageUri.name) ?: Uri.EMPTY)
    val tempImageUri: StateFlow<Uri> = _tempImageUri.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()


    private val _navigateActionRes = MutableSharedFlow<Int>()
    val navigateActionRes: SharedFlow<Int> = _navigateActionRes.asSharedFlow()

    private val _shortToastRes = MutableSharedFlow<Int>()
    val shortToastRes: SharedFlow<Int> = _shortToastRes.asSharedFlow()

    private val _logout = MutableSharedFlow<Boolean>()
    val logout: SharedFlow<Boolean> = _logout.asSharedFlow()

    // Lista de ejericicos

    var exercisesList = ArrayList<Exercise>()

    private var _selectedExercise = MutableStateFlow(Exercise())
    val selectedExercise: StateFlow<Exercise> = _selectedExercise.asStateFlow()

    fun setSelectedExercise(view:View,exercise: Exercise){
        _selectedExercise.value = exercise
        view.findNavController().navigate(R.id.action_myExercisesFragment_to_manageExerciseFragment)
    }
    fun setSelectedExercise(exercise: Exercise){
        _selectedExercise.value = exercise
    }

    fun deleteExercise(exercise: Exercise){

        viewModelScope.launch {
            listOf(
                async {
                    val result = userRepository.deleteExercise(exercise.id)
                    if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
                }
            ).awaitAll()
        }
    }
    ///

    // Lista de rutinas
    var routinesList = ArrayList<Routine>()

    private var _selectedRoutine = MutableStateFlow(Routine())
    val selectedRoutine: StateFlow<Routine> = _selectedRoutine.asStateFlow()

    fun setSelectedRoutine(view:View,routine: Routine){
        _selectedRoutine.value = routine
        view.findNavController().navigate(R.id.action_myRoutinesFragment_to_manageRoutineFragment)
    }
    fun setSelectedRoutine(routine: Routine){
        _selectedRoutine.value = routine
    }

    fun deleteRoutine(routine: Routine){

        viewModelScope.launch {
            listOf(
                async {
                    val result = userRepository.deleteRoutine(routine.id)
                    if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
                }
            ).awaitAll()
        }
    }

    ///

    //Obtiene un valor del usuario de DataStore instantáneamente
    suspend fun getStringUserDataStore(key: String): String {
        return userDataStore.getStringUserDataStoreOrEmpty(key)
    }

    fun fetchAll(email: String = user.value.email) {
        viewModelScope.launch {
            _loading.emit(true)

            listOf(
                async { fetchUserByEmail(email) }
            ).awaitAll()

            _loading.emit(false)
        }
    }

    private suspend fun fetchUserByEmail(email: String) {
        if (email.isNotEmpty()) { //Si el usuario no es invitado
            val resultUser = userRepository.fetchUserByEmail(email)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
                userDataStore.putString(DbConstants.USER_EMAIL, newUser.email)

                //Obtener rutinas del usuario
                val resultRoutines = userRepository.fetchRoutines(email)
                if (resultRoutines is DatabaseResult.Success) resultRoutines.data?.let { routines ->
                    routinesList = routines
                }else if (resultRoutines is DatabaseResult.Failed) _shortToastRes.emit(resultRoutines.resMessage)

                //Obtener ejercicios del usuario
                val resultExercises = userRepository.fetchExercises(email)
                if (resultExercises is DatabaseResult.Success) resultExercises.data?.let { exercises ->
                    exercisesList = exercises
                }else if (resultExercises is DatabaseResult.Failed) _shortToastRes.emit(resultExercises.resMessage)
            }
            else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    fun createExercise(ivExercise: ImageView, newExercise: Exercise) {
        viewModelScope.launch {
            _loading.emit(true)
            val resultUser = userRepository.uploadExerciseAndImage(user.value.email, newExercise,
                ivExercise, tempImageUri.value != Uri.EMPTY)
            _loading.emit(false)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
            }
            else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    fun createRoutine(ivRoutine: ImageView, newRoutine: Routine) {
        viewModelScope.launch {
            _loading.emit(true)
            val resultUser = userRepository.uploadRoutineAndImage(user.value.email, newRoutine,
                ivRoutine, tempImageUri.value != Uri.EMPTY)
            _loading.emit(false)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
            }
            else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    //Realiza el logout de FirebaseAuth y elimina el usuario de DataStore
    //Emite evento para lanzar StartActivity
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            userDataStore.deleteUserDataStore()
            _logout.emit(true)
        }
    }

    fun clearErrors(til: TextInputLayout) { til.error = "" }

    fun setTempImage(uri: Uri) { viewModelScope.launch { _tempImageUri.emit(uri) } }

    fun saveStateHandle() { savedStateHandle.set(::user.name, user.value) }

    fun navigate(navActionRes: Int) { viewModelScope.launch { _navigateActionRes.emit(navActionRes) } }

}