package es.ucm.fdi.workitout.repository

import android.util.Log
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.utils.orderRoutinesByWeekDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_USERS)
    private val dbExercises = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_EXERCISES)

    private val currentUser: FirebaseUser?
        get() = auth.currentUser

    //Se realiza el registro con el método de correo electrónico y contraseña
    suspend fun register(name: String, email: String, password: String): DatabaseResult<FirebaseUser?> {
        return try {
            withContext(Dispatchers.IO) {
                auth.signOut()
                auth.createUserWithEmailAndPassword(email, password).await()
                dbUsers.document(email).set(User(name = name)).await()
                DatabaseResult.success(auth.currentUser)
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
                DatabaseResult.success(auth.currentUser)
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
        return try {
            if (email == currentUser?.email) {
                withContext(Dispatchers.IO) {
                    var user: User? = User()
                    var routines: List<Routine> = emptyList()
                    var routinesScheduled: List<Routine> = emptyList()
                    listOf(
                        async { //Obtenemos el usuario de la sesión
                            val dsUser = dbUsers.document(email).get().await()
                            user = dsUser.toObject(User::class.java)?.copy(email = dsUser.id)
                        },
                        async { //Obtenemos las rutinas del usuario (Subcollections)
                            val qsRoutines = dbUsers.document(email)
                                .collection(DbConstants.USER_COLLECTION_ROUTINES).get().await()
                            routines = qsRoutines.toObjects(Routine::class.java).mapIndexed { indexRoutiens, routine ->
                                var exercises = emptyList<Exercise>()
                                if (routine.exercisesIds.isNotEmpty()) {
                                    val qsExercises = dbExercises.whereIn(FieldPath.documentId(),
                                        routine.exercisesIds).get().await()
                                    exercises = qsExercises.toObjects(Exercise::class.java)
                                        .mapIndexed { indexExercises, exercise ->
                                            exercise.copy(id = qsExercises.documents[indexExercises].id)
                                        }
                                }

                                routine.copy(id = qsRoutines.documents[indexRoutiens].id, exercises = exercises)
                            }
                            routinesScheduled = orderRoutinesByWeekDay(routines.filter { it.dayOfWeekScheduled != -1 })
                        }
                    ).awaitAll()
                    DatabaseResult.success(user?.copy( //Filtramos las rutinas programadas
                        routines = routines,
                        routinesScheduled = routinesScheduled
                    ))
                }
            } else {
                DatabaseResult.failed(R.string.error_fetch_user)
            }
        } catch (e: Exception) {
            Log.d("ASD", e.message.toString())
            DatabaseResult.failed(R.string.error_fetch_user)
        }
    }

    suspend fun checkAndUpdatePassword(currentPassword: String, newPassword: String): DatabaseResult<Unit> =
        try { withContext(Dispatchers.IO) {
            val user = currentUser
            val email = user?.email
            val credential = email?.let { EmailAuthProvider.getCredential(it, currentPassword) }
            if (user != null && email != null && credential != null) {
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword)
                DatabaseResult.success(Unit)
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

                DatabaseResult.success(Unit)
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
}