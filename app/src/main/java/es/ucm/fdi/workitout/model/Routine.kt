package es.ucm.fdi.workitout.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import es.ucm.fdi.workitout.utils.zeroTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Routine(
    @DocumentId var id: String = "",
    var name: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var exercisesSetsReps: List<ExerciseSetsReps> = emptyList(),
    var dayOfWeekScheduled: Int = -1,
    var timeTimestampScheduled: Timestamp = zeroTimestamp(),
    @get: Exclude var exercises: List<Exercise> = emptyList(),
) : Parcelable
