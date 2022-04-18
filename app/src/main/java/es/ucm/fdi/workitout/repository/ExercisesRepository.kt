package es.ucm.fdi.workitout.repository

import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.toObjectsArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExercisesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val dbExercises = db.collection(DbConstants.COLLECTION_EXERCISES)

    suspend fun fetchExercises(): DatabaseResult<ArrayList<Exercise>> =
        try { withContext(Dispatchers.IO) {
            DatabaseResult.success(dbExercises.get().await().toObjectsArrayList(Exercise::class.java))
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_fetch_exercises) }
}