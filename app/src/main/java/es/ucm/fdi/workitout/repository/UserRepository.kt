package es.ucm.fdi.workitout.repository

import android.util.Log
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_USERS)

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
                Log.d("ASD", email)
                withContext(Dispatchers.IO) {
                    val documentSnapshot = dbUsers.document(email).get().await()
                    val user = documentSnapshot.toObject(User::class.java)
                    Log.d("ASD2", user.toString())
                    DatabaseResult.success(user?.copy(email = documentSnapshot.id))
                }
            } else {
                DatabaseResult.failed(R.string.error_fetch_user)
            }
        } catch (e: Exception) {
            DatabaseResult.failed(R.string.error_fetch_user)
        }
    }

    //Se cierra sesión en Firebase Authentication
    fun logout() {
        auth.signOut()
    }
}