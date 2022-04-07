package es.ucm.fdi.workitout.repository

import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.User
import es.ucm.fdi.workitout.utils.getByteArray
import es.ucm.fdi.workitout.utils.getImageRef
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val dbUsers = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_USERS)

    suspend fun register(name: String, email: String, password: String) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            dbUsers.document(email).set(User(name = name)).await()
        } catch(e: Exception) {
            //TODO Mensaje error
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            //TODO Mensaje error
        }
    }

    suspend fun uploadExerciseAndImage(email: String, newExercise: Exercise, ivExercise: ImageView? = null, isNewImageUploaded: Boolean): DatabaseResult<User?> {
        return try { withContext(Dispatchers.IO) {
            val deferreds = ArrayList<Deferred<Any>>()
            if (isNewImageUploaded && ivExercise != null) { //Si hay nueva imagen se actualiza y se borra la anterior
                val byteArray = ivExercise.getByteArray(75)
                val imageRef = storage.getImageRef(DbConstants.COLLECTION_EXERCISES, newExercise.name)

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
                dbUsers.document(email).collection(DbConstants.USER_COLLECTION_EXERCISES)
                    .document(newExercise.id).set(newExercise).await()
            } else //Creamos ejercicio
                dbUsers.document(email).collection(DbConstants.USER_COLLECTION_EXERCISES)
                    .add(newExercise).await()

            DatabaseResult.success(User())
            //TODO Devolver el usuario actualizado con fetchUserByEmail(email)
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_upload_exercise) }
    }
}