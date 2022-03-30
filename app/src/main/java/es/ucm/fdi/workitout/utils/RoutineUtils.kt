package es.ucm.fdi.workitout.utils

import es.ucm.fdi.workitout.model.Routine
import java.time.LocalDateTime
import java.time.LocalTime

fun orderRoutinesByWeekDay(routines: List<Routine>): List<Routine> {
    val currentDayOfWeek = LocalDateTime.now().dayOfWeekIndex

    val routinesRet = ArrayList(routines.sortedBy { (7+it.dayOfWeekScheduled-currentDayOfWeek)%7 })
    if (routinesRet.isNotEmpty())
        if (routinesRet.first().timeTimestampScheduled.toDateTime().toLocalTime() < LocalTime.now().plusHours(1) &&
                routinesRet.first().dayOfWeekScheduled == currentDayOfWeek)
                    routinesRet.add(routinesRet.removeAt(0)) //La primera rutina pasa al final

    return routinesRet
}