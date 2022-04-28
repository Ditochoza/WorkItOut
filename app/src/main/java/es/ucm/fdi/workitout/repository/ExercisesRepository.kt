package es.ucm.fdi.workitout.repository

import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Record
import es.ucm.fdi.workitout.model.Video
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.toArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExercisesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val youtubeAPI = YoutubeAPI()
    private val dbUsers = db.collection(DbConstants.COLLECTION_USERS)
    private val dbExercises = db.collection(DbConstants.COLLECTION_EXERCISES)

    suspend fun fetchExercises(email: String): DatabaseResult<ArrayList<Exercise>> =
        try { withContext(Dispatchers.IO) {
            val records = dbUsers.document(email).collection(DbConstants.USER_COLLECTION_RECORDS)
                .get().await().toObjects(Record::class.java)

            DatabaseResult.successData(dbExercises.get().await().toObjects(Exercise::class.java).map { exercise ->
                val videos = ArrayList<Video>()
                exercise.videoLinks.forEach { videoLink ->
                    videos.add(youtubeAPI.fetchVideo(videoLink.videoUrl).copy(videoLink = videoLink))
                }
                val recordsExercise = records.filter { it.idExercise == exercise.id }
                    .sortedByDescending { it.timestamp }

                exercise.copy(videos = videos, records = recordsExercise)
            }.toArrayList())
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_fetch_exercises) }

    suspend fun updateExercise(newExercise: Exercise): DatabaseResult<Unit>  {
        return try { withContext(Dispatchers.IO) {
            dbExercises.document(newExercise.id).set(newExercise).await()

            DatabaseResult.successMessage(R.string.exercise_updated)
        } } catch (e: Exception) { DatabaseResult.failed(R.string.error_upload_exercise) }
    }
}