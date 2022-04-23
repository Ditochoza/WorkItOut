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
    @get: Exclude var videos: List<Video> = emptyList()
):Parcelable
