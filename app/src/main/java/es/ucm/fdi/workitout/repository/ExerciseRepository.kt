package es.ucm.fdi.workitout.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import es.ucm.fdi.workitout.model.Exercise
import kotlinx.coroutines.tasks.await


class ExerciseRepository {

    private val user = Firebase.auth.currentUser
    private val auth = FirebaseAuth.getInstance()

    private val dbUsers = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_USERS)
    private val dbExercises = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_EXERCISES)

    private var storage = Firebase.storage("gs://workitout-pad.appspot.com")

    /*
    * Regla de cloudstorage cambiada, esta es la que estaba:

    * */
    suspend fun saveExercise(exercise:Exercise) {
        try {
            try {
                val result = auth.signInWithEmailAndPassword("prueba@prueba.es", "123456")
            } catch (e: Exception) {
                //TODO Mensaje error
            }
            // Subir imagen y conseguir la url

            var file = exercise.image
            var storageRef = storage.reference
            val ref = storageRef.child("images/exercises/${file.lastPathSegment}")
            val uploadTask = ref.putFile(file)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //downloadUri
                    exercise.image = task.result
                } else {
                    // Handle failures
                }
            }

            //Subir ejercicio
            dbUsers.document()
            dbExercises.add(exercise).await()

            //Asociar el ejercicio creado con el usuario

            //dbCreatedExcercises.document(user.email).set()

        } catch(e: Exception) {
            //TODO Mensaje error
        }
    }
}
