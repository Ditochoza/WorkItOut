package es.ucm.fdi.workitout.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import es.ucm.fdi.workitout.utils.zeroTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Routine(
    @get: Exclude var id: String = "",
    var name: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var muscles: List<String> = emptyList(),
    @get: Exclude var dayOfWeekProgrammed: Int = -1,
    @get: Exclude var timeTimestampProgrammed: Timestamp = zeroTimestamp()
) : Parcelable
