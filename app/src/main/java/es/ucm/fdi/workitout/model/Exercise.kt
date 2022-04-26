package es.ucm.fdi.workitout.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    @DocumentId var id: String = "",
    var name: String = "",
    var description: String = "",
    var muscles: List<String>  = emptyList(),
    var imageUrl: String = "",
    var idUser: String = "",
    var videoLinks: List<VideoLink>  = emptyList(),
    var useReps: Boolean = false,
    var measureByReps: Boolean = false,
    var measureByTime: Boolean = false,
    var measureByWeight: Boolean = false,
    @get: Exclude var records: List<Record> = emptyList(),
    @get: Exclude var videos: List<Video> = emptyList(),
    @get: Exclude var tempExerciseRoutineSets: Int = 0, //Se setea sólo al crear el RecyclerView en rutina
    @get: Exclude var tempExerciseRoutineReps: Int = 0, //Se setea sólo al crear el RecyclerView en rutina
):Parcelable
