package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.*
import es.ucm.fdi.workitout.repository.ExercisesRepository
import es.ucm.fdi.workitout.repository.UserDataStore
import es.ucm.fdi.workitout.repository.UserRepository
import es.ucm.fdi.workitout.utils.*
import es.ucm.fdi.workitout.view.MainActivity
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

    private val _selectedRoutine = MutableStateFlow(savedStateHandle.get(::selectedRoutine.name) ?: Routine())
    val selectedRoutine: StateFlow<Routine> = _selectedRoutine.asStateFlow()

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

    fun addVideoToExercise(videoLink: String, exercise: Exercise) {
        val newVideoLinks = exercise.videoLinks.toMutableList()
        newVideoLinks.add(VideoLink(user.value.email, videoLink))
        val newExercise = exercise.copy(videoLinks = newVideoLinks)

        updateExercise(newExercise)
    }

    fun deleteVideoFromExercise(video: Video, exercise: Exercise) {
        val newVideoLinks = exercise.videoLinks.toMutableList()
        newVideoLinks.removeIf { it.videoUrl == video.url }
        val newExercise = exercise.copy(videoLinks = newVideoLinks)

        updateExercise(newExercise)
    }

    private fun updateExercise(newExercise: Exercise) {
        viewModelScope.launch {
            _loading.emit(true)
            val result =
                if (newExercise.idUser == user.value.email)
                    userRepository.uploadExerciseAndImage(user.value.email, newExercise, isNewImageUploaded = false)
                else exercisesRepository.updateExercise(newExercise)
            _loading.emit(false)

            if (result is DatabaseResult.SuccessMessage) {
                fetchAll()
                _shortToastRes.emit(result.resMessage)
            } else if(result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun deleteExercise(exercise: Exercise){
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.deleteExercise(exercise, user.value.email)
            _loading.emit(false)

            if (result is DatabaseResult.SuccessMessage) {
                _shortToastRes.emit(result.resMessage)
                fetchAll()
            } else if(result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
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
                async { fetchExercises(email) }
            ).awaitAll()

            //Rellenamos la información de ejercicios de las rutinas con los ejercicios obtenidos de la BBDD
            val routinesWithRecords =
                user.value.routines.putExercisesOnRoutines(user.value.exercises + exercises.value)
            val routinesWithRecordsScheduled =
                orderRoutinesByWeekDay(routinesWithRecords.filter { it.dayOfWeekScheduled != -1 })
            _user.value = user.value.copy(routines = routinesWithRecords, routinesScheduled = routinesWithRecordsScheduled)

            _loading.emit(false)
        }
    }

    private suspend fun fetchExercises(email: String) {
        val resultExercises = exercisesRepository.fetchExercises(email)
        if (resultExercises is DatabaseResult.SuccessData) {
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
            if (resultUser is DatabaseResult.SuccessData) resultUser.data?.let { newUser ->
                _user.value = newUser
                _selectedExercise.value =
                    user.value.exercises.firstOrNull { it.id == selectedExercise.value.id } ?:
                    exercises.value.firstOrNull { it.id == selectedExercise.value.id } ?:
                    Exercise()
                savedStateHandle.set(::user.name, user.value)
                savedStateHandle.set(::selectedExercise.name, selectedExercise.value)
                userDataStore.putString(DbConstants.USER_EMAIL, newUser.email)
            }
            else if (resultUser is DatabaseResult.Failed) _shortToastRes.emit(resultUser.resMessage)
        }
    }

    fun createExercise(ivExercise: ImageView, newExercise: Exercise) {
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.uploadExerciseAndImage(user.value.email, newExercise.copy(idUser = user.value.email),
                ivExercise, tempImageUri.value != Uri.EMPTY)
            _loading.emit(false)
            if (result is DatabaseResult.SuccessMessage) {
                _shortToastRes.emit(result.resMessage)
                fetchAll()
                _navigateActionRes.emit(0)
            }
            else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun deleteRoutine(routine: Routine){
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.deleteRoutine(routine, user.value.email)
            _loading.emit(false)

            if (result is DatabaseResult.SuccessMessage) {
                _shortToastRes.emit(result.resMessage)
                fetchAll()
            } else if(result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, alertDialog: AlertDialog) {
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.checkAndUpdatePassword(currentPassword, newPassword)
            _loading.emit(false)

            if (result is DatabaseResult.SuccessData) {
                _shortToastRes.emit(R.string.password_updated)
                alertDialog.dismiss()
            } else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun deleteAccount(currentPassword: String, alertDialog: AlertDialog) {
        viewModelScope.launch {
            val result = userRepository.checkPasswordAndDeleteAccount(currentPassword)

            if (result is DatabaseResult.SuccessData) {
                _shortToastRes.emit(R.string.user_deleted)
                alertDialog.dismiss()
                logout()
            } else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun createRoutine(ivRoutine: ImageView, newRoutine: Routine) {
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.uploadRoutineAndImage(user.value.email, newRoutine,
                ivRoutine, tempImageUri.value != Uri.EMPTY)
            _loading.emit(false)
            if (result is DatabaseResult.SuccessMessage) {
                _shortToastRes.emit(result.resMessage)
                fetchAll()
                _navigateActionRes.emit(0)
            } else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
        }
    }

    fun saveNewRecords(routine: Routine, activity: MainActivity) {
        val newRecords = ArrayList<Record>()
        routine.exercises.forEach { exercise ->
            val newRecord = exercise.records.firstOrNull { it.id.isEmpty() } ?: Record()
            val newRecordLogs = ArrayList<RecordLog>()
            newRecord.recordLogs.forEach { recordLog ->
                var saveRecordLog = true
                if (exercise.measureByReps || exercise.useReps) if (recordLog.repsLogged < 1) saveRecordLog = false
                if (exercise.measureByWeight) if (recordLog.weightLogged < 1) saveRecordLog = false
                if (exercise.measureByTime) if (recordLog.timeLogged < 1) saveRecordLog = false

                if (saveRecordLog)
                    newRecordLogs.add(RecordLog(
                        pos = 0,
                        repsLogged = if (exercise.measureByReps || exercise.useReps) recordLog.repsLogged else 0,
                        weightLogged = if (exercise.measureByWeight) recordLog.weightLogged else 0,
                        timeLogged = if (exercise.measureByTime) recordLog.timeLogged else 0,
                    ))
            }

            if (newRecordLogs.isNotEmpty())
                newRecords.add(newRecord.copy(
                    timestamp = Timestamp.now(),
                    idExercise = exercise.id,
                    recordLogs = newRecordLogs
                ))
        }

        activity.deleteNotification(routine.requestRoutineIdNotification)
        uploadRecords(newRecords, routine, activity)
    }

    private fun uploadRecords(newRecords: ArrayList<Record>, routine: Routine, activity: MainActivity) {
        viewModelScope.launch {
            _loading.emit(true)
            val result = userRepository.uploadRecords(user.value.email, newRecords)
            _loading.emit(false)
            if (result is DatabaseResult.SuccessMessage) {
                _shortToastRes.emit(result.resMessage)
                fetchAll()
                activity.deleteNotification(routine.requestRoutineIdNotification)

                activity.createTrainingNotification(
                    requestCode = routine.requestRoutineIdNotification,
                    title = activity.getString(R.string.records_training_saved),
                    message = activity.getString(R.string.routine_routine, routine.name)
                )
                _navigateActionRes.emit(R.id.action_trainingExercisesFragment_to_homeFragment)
            } else if (result is DatabaseResult.Failed) _shortToastRes.emit(result.resMessage)
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

    fun <T> setAndNavigate(something: T, navActionRes: Int) {
        set(something)
        navigate(navActionRes)
    }

    fun <T> set(something: T) {
        when (something) {
            is Exercise -> {
                _selectedExercise.value = something
                _tempImageUri.value = Uri.EMPTY
                savedStateHandle.set(::selectedExercise.name, selectedExercise.value)
            }
            is Routine -> {
                _selectedRoutine.value = something
                _tempImageUri.value = Uri.EMPTY
                savedStateHandle.set(::selectedRoutine.name, selectedRoutine.value)
            }
        }
    }

    fun navigate(navActionRes: Int) { viewModelScope.launch { _navigateActionRes.emit(navActionRes) } }

    fun clearErrors(til: TextInputLayout) { til.error = "" }

    fun setTempImage(uri: Uri) { viewModelScope.launch { _tempImageUri.emit(uri) } }

    fun saveStateHandle() { savedStateHandle.set(::user.name, user.value) } //TODO Añadir los demás StateFlows

    fun showToast(resMessage: Int) { viewModelScope.launch { _shortToastRes.emit(resMessage) } }
}