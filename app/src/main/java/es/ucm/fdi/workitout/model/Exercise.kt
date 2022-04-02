package es.ucm.fdi.workitout.model


import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    @get: Exclude var id: String = "",
    var name: String = "",
    var description: String = "",
    var muscles: List<String>  = listOf("biceps","triceps","abdominales", "gemelos","espalda","cuello","cabeza","piernas"),
    var image: String = "default",
    var videoLink: String = "https://www.youtube.com/watch?v=fsdsQfsdf",
):Parcelable
