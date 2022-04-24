package es.ucm.fdi.workitout.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseSetsReps(
    var idExercise: String = "",
    var sets: Int = 0,
    var reps: Int = 0,
):Parcelable
