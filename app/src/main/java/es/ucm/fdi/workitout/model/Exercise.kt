package es.ucm.fdi.workitout.model


import com.google.firebase.firestore.Exclude

data class Exercise(
    var name: String = "",
    var description: String = "",
    var muscles: List<String>  = listOf("biceps","triceps","abdominales", "gemelos","espalda","cuello","cabeza","piernas"),
    var image: String = "default",
    var videoLink: String = "https://www.youtube.com/watch?v=fsdsQfsdf",
)
