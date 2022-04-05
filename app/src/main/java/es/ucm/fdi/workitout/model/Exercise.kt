package es.ucm.fdi.workitout.model


import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    @get: Exclude var id: String = "",
    var name: String = "",
    var description: String = "",
    var muscles: List<String>  = listOf(),
    var image: Uri = Uri.EMPTY,
    var videoLinks: List<String>  = listOf()
):Parcelable
