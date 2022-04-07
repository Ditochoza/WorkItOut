package es.ucm.fdi.workitout.utils

import com.google.firebase.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


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

fun timeStringToDateTime(time: String): LocalDateTime =
    LocalDateTime.of(LocalDate.now(), LocalTime.parse(time, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))


/**
 * FORMATEOS
 */
fun LocalDateTime.timeString(): String =
    format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

fun Timestamp.timeString(): String =
    toDateTime().timeString()


/**
 * MODIFICACIONES
 */
fun LocalDateTime.withTime(hour: Int, minute: Int, second: Int): LocalDateTime =
    this.withHour(hour).withMinute(minute).withSecond(second)

fun LocalDateTime.withTime(dateTime: LocalDateTime): LocalDateTime =
    this.withTime(dateTime.hour, dateTime.minute, dateTime.second)

fun Timestamp.withTime(dateTime: LocalDateTime): Timestamp =
    this.toDateTime().withTime(dateTime).toTimestamp()