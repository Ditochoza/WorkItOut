package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.content.Intent
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
import es.ucm.fdi.workitout.model.*
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

    private var _selectedExercise = MutableStateFlow(Exercise())
    val selectedExercise: StateFlow<Exercise> = _selectedExercise.asStateFlow()
    private val _videoList = MutableStateFlow(emptyList<Video>())
    val videoList: StateFlow<List<Video>> = _videoList.asStateFlow()
    private val yotubeAPI = YoutubeAPI()

    fun setSelectedExercise(view:View,exercise: Exercise){
        _selectedExercise.value = exercise
        viewModelScope.launch {
            exercise.videoLinks.forEach { vlink ->
                val videoResult = yotubeAPI.getVideoInfo(vlink)
                if (videoResult is DatabaseResult.Success) videoResult.data?.let { video ->

                    var videoData = _videoList.value.toMutableList()
                    videoData.add(video)
                    _videoList.value = videoData

                }else{
                    var videoOffline = Video(url=vlink,title=vlink)
                    var videoData = _videoList.value.toMutableList()
                    videoData.add(videoOffline)
                    _videoList.value = videoData
                }
            }

        }
        view.findNavController().navigate(R.id.action_myExercisesFragment_to_manageExerciseFragment)
    }
    fun setSelectedExercise(exercise: Exercise){
        _selectedExercise.value = exercise
        viewModelScope.launch {
            exercise.videoLinks.forEach { vlink ->
                val videoResult = yotubeAPI.getVideoInfo(vlink)
                if (videoResult is DatabaseResult.Success) videoResult.data?.let { video ->

                    var videoData = _videoList.value.toMutableList()
                    videoData.add(video)
                    _videoList.value = videoData

                }else{
                    var videoOffline = Video(url=vlink,title=vlink)
                    var videoData = _videoList.value.toMutableList()
                    videoData.add(videoOffline)
                    _videoList.value = videoData
                }
            }

        }
    }

    fun deleteExercise(exercise: Exercise){

        viewModelScope.launch {

            val resultUser = userRepository.deleteExercise(exercise.id)
            if (resultUser is DatabaseResult.Success) resultUser.data?.let { newUser ->
                _user.value = newUser
                savedStateHandle.set(::user.name, user.value)

            }else if(resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)

        }
    }
    ///

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