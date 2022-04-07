package es.ucm.fdi.workitout.repository

import android.util.Log
import android.widget.ImageView
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.utils.getByteArray
import es.ucm.fdi.workitout.utils.getImageRef
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
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
                dbUsers.document(email).collection(DbConstants.USER_COLLECTION_ROUTINES)
                    .document(newRoutine.id).set(newRoutine).await()
            } else //Creamos rutina
                dbUsers.document(email).collection(DbConstants.USER_COLLECTION_ROUTINES)
                    .add(newRoutine).await()

            fetchUserByEmail(email)
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_upload_routine) }
    }
}