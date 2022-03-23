package es.ucm.fdi.workitout.utils

import com.google.firebase.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


val LocalDateTime.dayOfWeekIndex: Int
    get() {
        return when(this.dayOfWeek) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
            else -> -1
        }
    }

fun zeroDateTime(): LocalDateTime = zeroTimestamp().toDateTime()//LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault())

fun zeroTimestamp(): Timestamp = Timestamp(0, 0)


/**
 * CONVERSIONES
 */
fun LocalDateTime.toTimestamp() = Timestamp(this.atZone(ZoneId.systemDefault()).toEpochSecond(), this.nano)

fun Timestamp.toDateTime(): LocalDateTime =
    Instant.ofEpochSecond(this.seconds, this.nanoseconds.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()