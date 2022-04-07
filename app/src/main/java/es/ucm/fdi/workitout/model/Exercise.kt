package es.ucm.fdi.workitout.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    @get: Exclude var id: String = "",
    var name: String = "",
    var description: String = "",
    var muscles: List<String>  = emptyList(),
    var imageUrl: String = "",
    var videoLinks: List<String>  = emptyList()
):Parcelable