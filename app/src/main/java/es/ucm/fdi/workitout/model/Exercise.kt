package es.ucm.fdi.workitout.model


import com.google.firebase.firestore.Exclude

data class Exercise(
    var name: String = "",
    var description: String = "",
    var categories: Array<String> = listOf("artes marciales","futbol","natacion").toTypedArray(),
    var muscles: Array<String> = listOf("biceps","triceps","abdominales").toTypedArray(),
    var image: String = "default"
)
