package es.ucm.fdi.workitout.repository

import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RoutineRepository {

    private val auth = FirebaseAuth.getInstance()
    private val dbRoutines = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_ROUTINES)

    //Used to Create Routines
    private val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun addRoutine(routine: Routine): DatabaseResult<Routine?> {
        return try {
            withContext(Dispatchers.IO) {
                val routineParse = hashMapOf(
                    "name" to routine.name,
                    "description" to routine.description,
                    "image" to routine.image,
                    "hour" to routine.hour,
                    "days" to routine.days,
                    "user" to dbRoutines.doc("/users/" + currentUser?.email),
                    "exercises" to routine.exercises
                )
                dbRoutines.add(routineParse).await()
            }
        } catch (e: FirebaseFirestoreException) {
            DatabaseResult.failed(R.string.add_routine_error)
        }
    }
}