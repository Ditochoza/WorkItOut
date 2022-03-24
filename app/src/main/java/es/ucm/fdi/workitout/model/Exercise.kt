package es.ucm.fdi.workitout.model


import com.google.firebase.firestore.Exclude

data class Exercise(
    var name: String = "",
    var description: String = "",
    var muscles: List<String>  = listOf("biceps","triceps","abdominales"),
    var image: String = "default",
    var videoLinks: List<String> = listOf("www.youtube.com/v=fDsQ12Asd"),
)
