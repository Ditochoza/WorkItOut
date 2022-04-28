package es.ucm.fdi.workitout.utils

import com.google.firebase.Timestamp
import es.ucm.fdi.workitout.model.*
import java.time.LocalDateTime

fun orderRoutinesByWeekDay(routines: List<Routine>): List<Routine> {
    val currentDayOfWeek = LocalDateTime.now().dayOfWeekIndex

    val routinesRet = ArrayList(routines.sortedBy { (7+it.dayOfWeekScheduled-currentDayOfWeek)%7 })
    if (routinesRet.isNotEmpty()) {
        val currentTimeMinusOneHour = LocalDateTime.now().minusHours(1)
        val routineScheduledTime = LocalDateTime.now().withTime(routinesRet.first().timeTimestampScheduled.toDateTime())
        if (routineScheduledTime < currentTimeMinusOneHour &&
            routinesRet.first().dayOfWeekScheduled == currentDayOfWeek)
                routinesRet.add(routinesRet.removeAt(0)) //La primera rutina pasa al final
    }

    return routinesRet
}

fun List<Routine>.putExercisesOnRoutines(exercises: List<Exercise>): List<Routine> =
    this.map { routine ->
        val exercisesRoutine = exercises.filter { exercise ->
            routine.exercisesSetsReps.any { it.idExercise.contains(exercise.id) }
        }.map { exercise ->
            val exerciseSetsReps = routine.exercisesSetsReps
                .firstOrNull { it.idExercise.contains(exercise.id) }
                ?: ExerciseSetsReps()
            exercise.copy(
                tempExerciseRoutineSets = exerciseSetsReps.sets,
                tempExerciseRoutineReps = exerciseSetsReps.reps
            )
        }
        routine.copy(exercises = exercisesRoutine)
    }

fun Routine.getExercisesWithNewRecords() =
    this.exercises.map { exercise ->
        val newRecords = exercise.records.toMutableList()
        newRecords.removeIf { it.id.isEmpty() } //Eliminamos registros sin guardar
        newRecords.add(Record( //Añadimos un nuevo registro con los datos por defecto del ejercicio
            timestamp = Timestamp.now(),
            idExercise = exercise.id,
            recordLogs = List(exercise.tempExerciseRoutineSets) {
                RecordLog(
                    pos = it,
                    repsLogged = if (exercise.tempExerciseRoutineReps == 0 && !exercise.measureByWeight)
                        0 else exercise.tempExerciseRoutineReps,
                    weightLogged = if (exercise.measureByWeight) 0 else -1,
                    timeLogged = if (exercise.measureByTime) 0 else -1
                )
            }
        ))
        exercise.copy(records = newRecords)
    }

fun Routine.getExercisesWithNewRecords(pos: Int, reps: Int, weight: Int, time: Int, exercise: Exercise) =
    this.exercises.map {
        var newRecords = it.records
        if (it.id == exercise.id) {
            newRecords = exercise.records.map { record ->
                var newRecord = record
                if (record.id.isEmpty()) {
                    val newRecordLogs = record.recordLogs.toMutableList()
                    newRecordLogs[pos] = newRecordLogs[pos].copy(
                        repsLogged = reps,
                        weightLogged = weight,
                        timeLogged = time
                    )
                    newRecord = record.copy(recordLogs = newRecordLogs)
                }
                newRecord
            }
        }
        it.copy(records = newRecords)
    }