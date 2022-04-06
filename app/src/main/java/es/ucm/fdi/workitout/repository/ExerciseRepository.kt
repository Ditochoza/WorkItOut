package es.ucm.fdi.workitout.repository

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import es.ucm.fdi.workitout.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ExerciseRepository {

    private val user = Firebase.auth.currentUser
    private val auth = FirebaseAuth.getInstance()

    private val dbUsers = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_USERS)
    private val dbExercises = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_EXERCISES)

    private var storage = Firebase.storage("gs://workitout-pad.appspot.com")


    suspend fun saveExercise(exercise:Exercise,imageView:ImageView) {

        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val fileName = exercise.name.filter { !it.isWhitespace() } + dateTime

        try {
            // Subir imagen y conseguir la url


            var storageRef = storage.reference
            val ref = storageRef.child("images/exercises/$fileName")

            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageInByte: ByteArray = baos.toByteArray()

            val uploadTask = ref.putBytes(imageInByte).await()

            val urlTask = uploadTask.task.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Subir ejercicio
                    exercise.image =  task.result.toString()
                    dbExercises.add(exercise)

                } else {
                    // Handle failures
                }
            }




        } catch(e: Exception) {
            //TODO Mensaje error
        }
    }
}
