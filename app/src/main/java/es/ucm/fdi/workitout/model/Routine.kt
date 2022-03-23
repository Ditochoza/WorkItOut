package es.ucm.fdi.workitout.model

import java.time.DayOfWeek

data class Routine (
    var name: String = "",
    var description: String = "",
    var image: String = "",
    var hour: String = "",
    var days: Array<DayOfWeek> = listOf(DayOfWeek.MONDAY,DayOfWeek.WEDNESDAY,DayOfWeek.FRIDAY).toTypedArray(),
    var user: String = "ramonros@ucm.es",
    //var exercises: Array<Exercise> = listOf().toTypedArray()
)