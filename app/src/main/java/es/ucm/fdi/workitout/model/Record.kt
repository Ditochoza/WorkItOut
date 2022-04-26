package es.ucm.fdi.workitout.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import es.ucm.fdi.workitout.utils.zeroTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Record(
    @DocumentId var id: String = "",
    var timestamp: Timestamp = zeroTimestamp(),
    var idExercise: String = "",
    var recordLogs: List<RecordLog> = emptyList(),
):Parcelable
