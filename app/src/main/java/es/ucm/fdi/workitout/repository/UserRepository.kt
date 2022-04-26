package es.ucm.fdi.workitout.repository

import android.widget.ImageView
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.*
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.DbConstants.COLLECTION_EXERCISES
import es.ucm.fdi.workitout.utils.DbConstants.COLLECTION_USERS
import es.ucm.fdi.workitout.utils.DbConstants.USER_COLLECTION_EXERCISES
import es.ucm.fdi.workitout.utils.DbConstants.USER_COLLECTION_RECORDS
import es.ucm.fdi.workitout.utils.DbConstants.USER_COLLECTION_ROUTINES
import es.ucm.fdi.workitout.utils.getByteArray
import es.ucm.fdi.workitout.utils.getImageRef
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val youtubeAPI = YoutubeAPI()
    private val dbUsers = db.collection(COLLECTION_USERS)
    private val dbExercises = db.collection(COLLECTION_EXERCISES)

    private val currentUser: FirebaseUser?
        get() = auth.currentUser

    //Se realiza el registro con el método de correo electrónico y contraseña
    suspend fun register(name: String, email: String, password: String): DatabaseResult<FirebaseUser?> {
        return try {
            withContext(Dispatchers.IO) {
                auth.signOut()
                auth.createUserWithEmailAndPassword(email, password).await()
                dbUsers.document(email).set(User(name = name)).await()
                DatabaseResult.successData(auth.currentUser)
            }
        } catch (e: FirebaseAuthUserCollisionException) { //Ya existe un usuario con ese correo electrónico
            DatabaseResult.failed(R.string.existing_user_login_exception)
        } catch (e: FirebaseAuthInvalidCredentialsException) { //El formato del email no es válido
            DatabaseResult.failed(R.string.invalid_email)
        } catch (e: Exception) {
            DatabaseResult.failed(R.string.register_error)
        }
    }

    //Se inicia sesión con el método de correo electrónico y contraseña
    suspend fun login(email: String, password: String): DatabaseResult<FirebaseUser?> {
        return try {
            withContext(Dispatchers.IO) {
                auth.signOut()
                auth.signInWithEmailAndPassword(email, password).await()
                DatabaseResult.successData(auth.currentUser)
            }
        } catch (e: FirebaseTooManyRequestsException) { //Cuenta bloqueada por demasiados intentos fallidos
            DatabaseResult.failed(R.string.too_many_attempts_try_again_later_exception)
        } catch (e: FirebaseAuthInvalidUserException) { //No existe una cuenta con ese correo electrónico
            DatabaseResult.failed(R.string.invalid_user_login_exception)
        } catch (e: FirebaseAuthInvalidCredentialsException) { //La contraseña introducida no es correcta
            DatabaseResult.failed(R.string.invalid_password_exception)
        } catch (e: Exception) {
            DatabaseResult.failed(R.string.error_login)
        }
    }

    //Devuelve el usuario de la base de datos asociado a un correo electrónico
    suspend fun fetchUserByEmail(email: String): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            if (email == currentUser?.email) {
                var user: User? = User()
                var routines: List<Routine> = emptyList()
                var exercises: List<Exercise> = emptyList()
                listOf(
                    async { //Obtenemos el usuario de la sesión
                        user = dbUsers.document(email).get().await().toObject(User::class.java)
                    },
                    async { //Obtenemos los ejercicios del usuario (Subcollection)
                        val records = dbUsers.document(email).collection(USER_COLLECTION_RECORDS)
                            .get().await().toObjects(Record::class.java)

                        exercises = dbUsers.document(email).collection(USER_COLLECTION_EXERCISES)
                            .get().await().toObjects(Exercise::class.java).map { exercise ->
                                val videos = ArrayList<Video>()
                                exercise.videoLinks.forEach { videoLink ->
                                    videos.add(youtubeAPI.fetchVideo(videoLink.videoUrl).copy(videoLink = videoLink))
                                }
                                val recordsExercise = records.filter { it.idExercise == exercise.id }

                                exercise.copy(videos = videos, records = recordsExercise)
                            }
                    },
                    async { //Obtenemos las rutinas del usuario (Subcollection)
                        routines = dbUsers.document(email)
                            .collection(USER_COLLECTION_ROUTINES).get().await()
                            .toObjects(Routine::class.java)
                    },
                ).awaitAll()
                DatabaseResult.successData(
                    user?.copy(
                        routines = routines,
                        exercises = exercises
                    )
                )
            } else {
                DatabaseResult.failed(R.string.error_no_email_found)
            }
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_fetch_user) }
    }

    suspend fun deleteExercise(exercise: Exercise, email: String): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            listOf(
                async { //Eliminamos el ejercicio
                    dbUsers.document(email).collection(USER_COLLECTION_EXERCISES).document(exercise.id).delete()
                },
                async { //Eliminamos la imagen del ejercicio
                    if (exercise.imageUrl.isNotEmpty()) {
                        storage.getReferenceFromUrl(exercise.imageUrl).delete().await()
                    }
                },
            ).awaitAll()

            DatabaseResult.successMessage(R.string.exercise_deleted)
        } } catch (e: java.lang.Exception) { DatabaseResult.failed(R.string.exercise_could_not_be_deleted) }
    }

    suspend fun uploadExerciseAndImage(email: String, newExercise: Exercise, ivExercise: ImageView? = null, isNewImageUploaded: Boolean): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            val deferreds = ArrayList<Deferred<Any>>()
            if (isNewImageUploaded && ivExercise != null) { //Si hay nueva imagen se actualiza y se borra la anterior
                val byteArray = ivExercise.getByteArray(75)
                val imageRef = storage.getImageRef(COLLECTION_EXERCISES, newExercise.name)

                if (newExercise.imageUrl.isNotEmpty())
                    deferreds.add( //Si se está actualizando la imagen se elimina la anterior
                        async { storage.getReferenceFromUrl(newExercise.imageUrl).delete().await() }
                    )

                deferreds.add(
                    async { //Subimos la nueva imagen
                        imageRef.putBytes(byteArray).await()
                        val newImageUrl = imageRef.downloadUrl.await().toString()
                        //Una vez subida la imagen se sube el ejercicio con la URL obtenida
                        newExercise.imageUrl = newImageUrl
                    }
                )
            }
            deferreds.awaitAll() //Esperamos a que se complete la subida de imagen para subir el ejercicio

            if (newExercise.id.isNotEmpty()) { //Actualizamos ejercicio
                dbUsers.document(email).collection(USER_COLLECTION_EXERCISES)
                    .document(newExercise.id).set(newExercise).await()
            } else //Creamos ejercicio
                dbUsers.document(email).collection(USER_COLLECTION_EXERCISES)
                    .add(newExercise).await()

            DatabaseResult.successMessage(R.string.exercise_updated)
        } } catch (e: Exception) {
            print(e.message)
            DatabaseResult.failed(R.string.error_upload_exercise) }
    }

    suspend fun deleteRoutine(routine: Routine, email: String): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            listOf(
                async { //Eliminamos la rutina
                    dbUsers.document(email).collection(USER_COLLECTION_ROUTINES).document(routine.id).delete().await()
                },
                async { //Eliminamos la imagen de la rutina
                    if (routine.imageUrl.isNotEmpty()) {
                        storage.getReferenceFromUrl(routine.imageUrl).delete().await()
                    }
                },
            ).awaitAll()

            DatabaseResult.successMessage(R.string.routine_deleted)
        } } catch (e: java.lang.Exception) { DatabaseResult.failed(R.string.routine_could_not_be_deleted) }
    }

    suspend fun uploadRoutineAndImage(email: String, newRoutine: Routine, ivRoutine: ImageView? = null, isNewImageUploaded: Boolean): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            val deferreds = ArrayList<Deferred<Any>>()
            if (isNewImageUploaded && ivRoutine != null) { //Si hay nueva imagen se actualiza y se borra la anterior
                val byteArray = ivRoutine.getByteArray(75)
                val imageRef = storage.getImageRef(DbConstants.COLLECTION_ROUTINES, newRoutine.name)

                if (newRoutine.imageUrl.isNotEmpty())
                    deferreds.add( //Si se está actualizando la imagen se elimina la anterior
                        async { storage.getReferenceFromUrl(newRoutine.imageUrl).delete().await() }
                    )

                deferreds.add(
                    async { //Subimos la nueva imagen
                        imageRef.putBytes(byteArray).await()
                        val newImageUrl = imageRef.downloadUrl.await().toString()
                        //Una vez subida la imagen se sube la rutina con la URL obtenida
                        newRoutine.imageUrl = newImageUrl
                    }
                )
            }
            deferreds.awaitAll() //Esperamos a que se complete la subida de imagen para subir la rutina

            if (newRoutine.id.isNotEmpty()) { //Actualizamos rutina
                dbUsers.document(email).collection(USER_COLLECTION_ROUTINES)
                    .document(newRoutine.id).set(newRoutine).await()
            } else //Creamos rutina
                dbUsers.document(email).collection(USER_COLLECTION_ROUTINES)
                    .add(newRoutine).await()

            DatabaseResult.successMessage(R.string.routine_updated)
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_upload_routine) }
    }

    suspend fun checkAndUpdatePassword(currentPassword: String, newPassword: String): DatabaseResult<Unit> =
        try { withContext(Dispatchers.IO) {
            val user = currentUser
            val email = user?.email
            val credential = email?.let { EmailAuthProvider.getCredential(it, currentPassword) }
            if (user != null && email != null && credential != null) {
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword)
                DatabaseResult.successData(Unit)
            } else {
                DatabaseResult.failed(R.string.error_update_password)
            }
        } } catch (e: FirebaseTooManyRequestsException) { //Cuenta bloqueada por demasiados intentos fallidos
            DatabaseResult.failed(R.string.too_many_attempts_try_again_later_exception)
        } catch (e: FirebaseAuthInvalidCredentialsException) { //La contraseña introducida no es correcta
            DatabaseResult.failed(R.string.invalid_password_exception)
        } catch (e: Exception) { DatabaseResult.failed(R.string.error_update_password) }

    suspend fun checkPasswordAndDeleteAccount(password: String): DatabaseResult<Unit> =
        try { withContext(Dispatchers.IO) {
            val user = currentUser
            val email = user?.email
            val credential = email?.let { EmailAuthProvider.getCredential(it, password) }
            if (user != null && email != null && credential != null) {
                user.reauthenticate(credential).await()
                user.delete().await()
                dbUsers.document(email).delete().await()
                //TODO Eliminar rutinas y ejercicios del usuario

                DatabaseResult.successData(Unit)
            } else {
                DatabaseResult.failed(R.string.error_delete_user)
            }
        } } catch (e: FirebaseTooManyRequestsException) { //Cuenta bloqueada por demasiados intentos fallidos
            DatabaseResult.failed(R.string.too_many_attempts_try_again_later_exception)
        } catch (e: FirebaseAuthInvalidCredentialsException) { //La contraseña introducida no es correcta
            DatabaseResult.failed(R.string.invalid_password_exception)
        } catch (e: Exception) { DatabaseResult.failed(R.string.error_delete_user) }

    //Se cierra sesión en Firebase Authentication
    fun logout() {
        auth.signOut()
    }

    suspend fun uploadRecords(email: String, newRecords: ArrayList<Record>): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            val batch = db.batch()

            newRecords.forEach { record ->
                val newRecordRef = dbUsers.document(email).collection(USER_COLLECTION_RECORDS).document()
                batch.set(newRecordRef, record)
            }

            batch.commit().await()

            DatabaseResult.successMessage(R.string.training_uploaded)
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_uploading_training) }
    }
}