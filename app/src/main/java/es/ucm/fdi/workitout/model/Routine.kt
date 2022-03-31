package es.ucm.fdi.workitout.model

import java.time.DayOfWeek

data class Routine (
    var name: String = "",
    var description: String = "",
    var image: String= "",
    var hour: String= "",
    var days: List<DayOfWeek> = ArrayList(),
    var user: User? = null,
    var exercises: List<Exercise> = ArrayList()
)