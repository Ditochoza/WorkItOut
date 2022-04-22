package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.model.*
import es.ucm.fdi.workitout.repository.ExercisesRepository
import es.ucm.fdi.workitout.repository.UserDataStore
import es.ucm.fdi.workitout.repository.UserRepository
import es.ucm.fdi.workitout.repository.YoutubeAPI
import es.ucm.fdi.workitout.utils.DbConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainSharedViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application)  {
    private val userRepository = UserRepository()
    private val exercisesRepository = ExercisesRepository()
    private val userDataStore by lazy { UserDataStore(application) }

    private val _user = MutableStateFlow(savedStateHandle.get(::user.name) ?: User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _exercises = MutableStateFlow<ArrayList<Exercise>>(savedStateHandle.get(::exercises.name) ?: ArrayList())
    val exercises: StateFlow<ArrayList<Exercise>> = _exercises.asStateFlow()

    private val _selectedExercise = MutableStateFlow(savedStateHandle.get(::selectedExercise.name) ?: Exercise())
    val selectedExercise: StateFlow<Exercise> = _selectedExercise.asStateFlow()

    private val _routines = MutableStateFlow<ArrayList<Routine>>(savedStateHandle.get(::routines.name) ?: ArrayList())
    val routines: StateFlow<ArrayList<Routine>> = _routines.asStateFlow()

    private val _selectedRoutine = MutableStateFlow(savedStateHandle.get(::selectedRoutine.name) ?: Routine())
    val selectedRoutine: StateFlow<Routine> = _selectedRoutine.asStateFlow()

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

    private val _videoList = MutableStateFlow(emptyList<Video>())
    val videoList: StateFlow<List<Video>> = _videoList.asStateFlow()
    private val yotubeAPI = YoutubeAPI()


    fun getVideoData(exercise: Exercise){
        //Vaciamos la lista de links
        _videoList.value = emptyList<Video>()

        viewModelScope.launch {
            exercise.videoLinks.forEach { vlinkObj ->
                val vUrl = vlinkObj.videoUrl
                val videoResult = yotubeAPI.getVideoInfo(vUrl)
                if (videoResult is DatabaseResult.Success) videoResult.data?.let { video ->

                    var videoData = _videoList.value.toMutableList()
                    video.videoLink = vlinkObj
                    videoData.add(video)
                    _videoList.value = videoData

                }else{
                    var videoOffline = Video(url=vUrl,title=vUrl)
                    var videoData = _videoList.value.toMutableList()
                    videoData.add(videoOffline)
                    _videoList.value = videoData
                }
            }

        }
    }

    fun deleteVideoFromExercise(videoLink: VideoLink){
        var exercise:Exercise = selectedExercise.value
        viewModelScope.launch {
            val resultUser = userRepository.deleteExerciseVideo(exercise, videoLink)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
            } else if(resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)

        }
    }



    fun deleteExercise(exercise: Exercise){

        viewModelScope.launch {
            _loading.emit(true)
            val resultUser = userRepository.deleteExercise(exercise, user.value.email)
            _loading.emit(false)

            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
            } else if(resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)

        }
    }

    fun goToVideoLink (view: View, url:String){
        val urlUri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, urlUri)
        view.context.startActivity(intent)
    }

    //Obtiene un valor del usuario de DataStore instantáneamente
    suspend fun getStringUserDataStore(key: String): String {
        return userDataStore.getStringUserDataStoreOrEmpty(key)
    }

    fun fetchAll(email: String = user.value.email) {
        viewModelScope.launch {
            _loading.emit(true)

            listOf(
                async { fetchUserByEmail(email) },
                async { fetchExercises() }
            ).awaitAll()

            _loading.emit(false)
        }
    }

    private suspend fun fetchExercises() {
        val resultExercises = exercisesRepository.fetchExercises()
        if (resultExercises is DatabaseResult.Success) {
            _exercises.value = resultExercises.data
            _selectedExercise.value =
                user.value.exercises.firstOrNull { it.id == selectedExercise.value.id } ?:
                exercises.value.firstOrNull { it.id == selectedExercise.value.id } ?:
                Exercise()
            savedStateHandle.set(::exercises.name, exercises.value)
            savedStateHandle.set(::selectedExercise.name, selectedExercise.value)
        }
        else if (resultExercises is DatabaseResult.Failed) _shortToastRes.emit(resultExercises.resMessage)
    }

    private suspend fun fetchUserByEmail(email: String) {
        if (email.isNotEmpty()) { //Si el usuario no es invitado
            val resultUser = userRepository.fetchUserByEmail(email)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
                userDataStore.putString(DbConstants.USER_EMAIL, newUser.email)
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

    fun deleteRoutine(routine: Routine){
        viewModelScope.launch {
            _loading.emit(true)
            val resultUser = userRepository.deleteRoutine(routine, user.value.email)
            _loading.emit(false)

            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)
            } else if(resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, alertDialog: AlertDialog) {
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.checkAndUpdatePassword(currentPassword, newPassword)
            _loading.emit(false)

            if (result is DatabaseResult.Success) {
                _shortToastRes.emit(R.string.password_updated)
                alertDialog.dismiss()
            } else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun deleteAccount(currentPassword: String, alertDialog: AlertDialog) {
        viewModelScope.launch {
            val result = userRepository.checkPasswordAndDeleteAccount(currentPassword)

            if (result is DatabaseResult.Success) {
                _shortToastRes.emit(R.string.user_deleted)
                alertDialog.dismiss()
                logout()
            } else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
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
            } else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
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

    fun <T> navigateAndSet(something: T, navActionRes: Int) {
        when (something) {
            is Exercise -> {
                _selectedExercise.value = something
                getVideoData(selectedExercise.value)
                savedStateHandle.set(::selectedExercise.name, selectedExercise.value)
            }
        }

        navigate(navActionRes)
    }

    fun clearErrors(til: TextInputLayout) { til.error = "" }

    fun setTempImage(uri: Uri) { viewModelScope.launch { _tempImageUri.emit(uri) } }

    fun saveStateHandle() { savedStateHandle.set(::user.name, user.value) }

    fun navigate(navActionRes: Int) { viewModelScope.launch { _navigateActionRes.emit(navActionRes) } }
}