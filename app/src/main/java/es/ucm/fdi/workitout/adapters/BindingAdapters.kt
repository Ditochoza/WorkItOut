package es.ucm.fdi.workitout.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.*
import es.ucm.fdi.workitout.utils.getColorFromAttr
import es.ucm.fdi.workitout.utils.loadResource
import es.ucm.fdi.workitout.view.*
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel
import es.ucm.fdi.workitout.viewModel.CreateRoutineViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

@BindingAdapter("sModel", "navActionResToSettings", requireAll = true)
fun MaterialToolbar.onClickMainMenu(sModel: MainSharedViewModel, navActionResToSettings: Int) {
    this.menu.findItem(R.id.item_update_menu_main).setOnMenuItemClickListener {
        sModel.fetchAll()
        return@setOnMenuItemClickListener true
    }
    this.menu.findItem(R.id.item_logout_menu_main).setOnMenuItemClickListener {
        sModel.logout()
        return@setOnMenuItemClickListener true
    }
    this.menu.findItem(R.id.item_settings_menu_main).setOnMenuItemClickListener {
        sModel.navigate(navActionResToSettings)
        return@setOnMenuItemClickListener true
    }
}

@BindingAdapter("sModel", "navActionResToEdit", "navActionResToRecords", "exerciseOrRoutine", requireAll = true)
fun MaterialToolbar.onClickEditMenu(sModel: MainSharedViewModel, navActionResToEdit: Int,
                                    navActionResToRecords: Int, exerciseOrRoutine: Any) {

    if (exerciseOrRoutine is Exercise) { //Ocultamos el botón de editar ejercicio si estamos en un entrenamiento
        val selectedExercise = sModel.selectedRoutine.value.exercises.firstOrNull { it.id == exerciseOrRoutine.id } ?: Exercise()
        if (selectedExercise.records.any { it.id.isEmpty() })
            this.menu.findItem(R.id.item_edit_menu_edit).isVisible = false
    }
    if (exerciseOrRoutine is Routine) this.menu.findItem(R.id.item_logs_menu_edit).isVisible = false

    this.menu.findItem(R.id.item_edit_menu_edit).setOnMenuItemClickListener {
        sModel.setAndNavigate(exerciseOrRoutine, navActionResToEdit)
        return@setOnMenuItemClickListener true
    }
    this.menu.findItem(R.id.item_logs_menu_edit).setOnMenuItemClickListener {
        sModel.navigate(navActionResToRecords)
        return@setOnMenuItemClickListener true
    }
}

@BindingAdapter("sModel", "navActionResToView", requireAll = true)
fun MaterialToolbar.onClickInfoMenu(sModel: MainSharedViewModel, navActionResToView: Int) {
    this.menu.findItem(R.id.item_info_menu_info).setOnMenuItemClickListener {
        sModel.navigate(navActionResToView)
        return@setOnMenuItemClickListener true
    }
}

//BindingAdapter para colocar los músculos para seleccionar en el ChipGroup de CreateExercise
//También se le añade un listener para actualizar los músculos seleccionados
@BindingAdapter("muscles", "vModel", requireAll = true)
fun ChipGroup.adapterChipsMusclesSelect(muscles: Array<String>, vModel: CreateExerciseViewModel) {
    removeAllViews()
    muscles.forEach {
        addView(Chip(context).apply {
            text = it
            isClickable = true
            isCheckable = true
            isSelectionRequired = true
            setChipDrawable(ChipDrawable.createFromAttributes(context,
                null,
                0,
                R.style.Widget_Material3_Chip_Filter
            ))
            isChecked = vModel.tempExercise.value.muscles.contains(it)
        })
    }
    setOnCheckedStateChangeListener { _, checkedIds ->
        val newMuscles = ArrayList<String>()
        checkedIds.forEach { chipId -> newMuscles.add(findViewById<Chip>(chipId).text.toString()) }
        vModel.updateMuscles(newMuscles)
    }
}

@BindingAdapter("muscles")
fun ChipGroup.adapterChipsMuscles(muscles: List<String>) {
    removeAllViews()
    muscles.forEach {
        addView(Chip(context).apply {
            text = it
        })
    }

}


//BindingAdapter para colocar una imagen en el ImageView a partir de su URL (HTTP) o Uri de imagen elegida
@BindingAdapter("imageUrl", "tempImageUri")
fun ImageView.loadImage(imageUrl: String, tempImageUri: Uri) {
    if (tempImageUri != Uri.EMPTY) this.setImageURI(tempImageUri)
    else if (imageUrl.isNotEmpty()) this.loadResource(imageUrl)
}

//BindingAdapter para colocar una imagen en el ImageView a partir de su URL (HTTP)
@BindingAdapter("imageUrl")
fun ImageView.loadImage(imageUrl: String) {
    if (imageUrl.isNotEmpty()) this.loadResource(imageUrl)
}

@BindingAdapter("minValue", "maxValue", requireAll = true)
fun NumberPicker.adapterNumberPicker(minValue: Int, maxValue: Int) {
    setMinValue(minValue)
    setMaxValue(maxValue)
}

@BindingAdapter("recordLogs")
fun LinearLayout.adapterRecordLogs(recordLogs: List<RecordLog>) {
    removeAllViews()
    recordLogs.forEachIndexed { index, recordLog ->
        val recordLogsStrings = ArrayList<String>()
        if (recordLog.repsLogged > 0) recordLogsStrings.add(context.getString(R.string.reps_number, recordLog.repsLogged))
        if (recordLog.weightLogged > 0) recordLogsStrings.add(context.getString(R.string.weight_number, recordLog.weightLogged))
        if (recordLog.timeLogged > 0) recordLogsStrings.add(context.getString(R.string.time_number, recordLog.timeLogged))
        val textRecordLog = "${index+1}. ${recordLogsStrings.joinToString(" - ")}"
        val textView = LayoutInflater.from(context).inflate(R.layout.record_log_text_view, this, false) as TextView
        addView(textView.apply {
            text = textRecordLog
        })
    }
}

//BindingAdapter para mostrar los chips con los músculos de las rutinas
@BindingAdapter("exercises")
fun ChipGroup.adapterChipsExercisesMuscles(exercises: List<Exercise>) {
    removeAllViews()
    val muscles = exercises.flatMap { it.muscles }.distinct()
    muscles.forEach {
        val chip = LayoutInflater.from(context).inflate(R.layout.muscle_chip, this, false) as Chip
        addView(chip.apply {
            text = it
            isClickable = false
        })
    }
}

//BindingAdapter para configurar el adaptador del RecyclerView de rutinas programadas
@BindingAdapter("routines", "sModel", "scheduled", "fragment", requireAll = true)
fun RecyclerView.adapterScheduledRoutines(routines: List<Routine>, sModel: MainSharedViewModel, scheduled: Boolean, fragment: HomeFragment) {
    val routinesArrayList = if (scheduled && routines.size > 1) { //Sólo las rutinas programadas (A partir de la 1)
        ArrayList(routines.subList(1, routines.size))
    } else { //Todas las rutinas
        ArrayList(routines)
    }

    if (this.adapter == null)
        this.adapter = ScheduledRoutinesRecyclerViewAdapter(routinesArrayList, sModel, fragment)
    else
        (adapter as ScheduledRoutinesRecyclerViewAdapter).updateList(routinesArrayList)
}

@BindingAdapter("weekDays","vModel","til", requireAll = true)
fun AutoCompleteTextView.adapterWeekDays(weekDays: Array<String>, vModel: CreateRoutineViewModel, til: TextInputLayout) {
    setAdapter(ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, weekDays))
    if (vModel.tempRoutine.value.dayOfWeekScheduled != -1)
        setText(weekDays[vModel.tempRoutine.value.dayOfWeekScheduled])
    setOnItemClickListener { _, _, i, _ ->
        vModel.updateWeekDay(i)
        til.error = ""
    }
}

@BindingAdapter("checkedItem")
fun NavigationView.adapterNavigationView(checkedItem: Int) {
    setCheckedItem(checkedItem)
}

@BindingAdapter("sModel", "exercises", "myExercises", "routine", "fragment", requireAll = true)
fun RecyclerView.adapterSelectExercises(sModel: MainSharedViewModel, exercises: List<Exercise>,
                                        myExercises: List<Exercise>, routine: Routine,
                                        fragment: SelectExercisesFragment) {
    var exercisesList = exercises + myExercises
    if (routine != Routine()) {
        exercisesList = exercisesList.map { exercise ->
            val exerciseSetsReps = routine.exercisesSetsReps
                .firstOrNull { it.idExercise.contains(exercise.id) } ?: ExerciseSetsReps()
            exercise.copy(
                tempExerciseRoutineSets = exerciseSetsReps.sets,
                tempExerciseRoutineReps = exerciseSetsReps.reps
            )
        }
    }

    if (this.adapter == null)
        this.adapter = SelectExercisesRecyclerViewAdapter(exercisesList.toList(),
            routine, sModel, fragment, context.getColorFromAttr(R.attr.colorSurfaceVariant),
            context.getColorFromAttr(R.attr.colorTertiaryContainer))
    else
        (adapter as SelectExercisesRecyclerViewAdapter).updateList(exercisesList.toList(), routine)
}

@BindingAdapter("sModel", "exercises", "myExercises", "activity", "navActionResToEdit", "navActionResToView", "routine", requireAll = true)
fun RecyclerView.adapterExercises(sModel: MainSharedViewModel, exercises: List<Exercise>,
                                  myExercises: List<Exercise>, activity: MainActivity,
                                  navActionResToEdit: Int, navActionResToView: Int, routine: Routine) {
    var exercisesList = exercises + myExercises
    if (routine != Routine()) {
        exercisesList = exercisesList.map { exercise ->
            val exerciseSetsReps = routine.exercisesSetsReps
                .firstOrNull { it.idExercise.contains(exercise.id) } ?: ExerciseSetsReps()
            exercise.copy(
                tempExerciseRoutineSets = exerciseSetsReps.sets,
                tempExerciseRoutineReps = exerciseSetsReps.reps
            )
        }
    }

    if (this.adapter == null)
        this.adapter = ExercisesRecyclerViewAdapter(exercisesList, sModel,
            activity, navActionResToEdit, navActionResToView)
    else
        (adapter as ExercisesRecyclerViewAdapter).updateList(exercisesList)
}

@BindingAdapter("sModel", "exercise", "records", "routine", "fragment", requireAll = true)
fun RecyclerView.adapterTrainingRecords(sModel: MainSharedViewModel, exercise: Exercise,
                                        records: List<Record>, routine: Routine, fragment: TrainingExerciseFragment) {
    val recordLogs = (records.firstOrNull { it.id.isEmpty() } ?: Record()).recordLogs
    val colorCardDefault = context.getColorFromAttr(R.attr.colorSurfaceVariant)
    val colorCardLogged = context.getColorFromAttr(R.attr.colorAccent)
    val colorTextDefault = context.getColorFromAttr(R.attr.colorOnSurface)
    val colorTextLogged = context.getColorFromAttr(R.attr.colorOnSecondary)

    if (this.adapter == null)
        this.adapter = TrainingRecordsRecyclerViewAdapter(recordLogs, exercise, routine, colorCardDefault,
            colorCardLogged, colorTextDefault, colorTextLogged, sModel, fragment)
    else
        (adapter as TrainingRecordsRecyclerViewAdapter).updateList(recordLogs, routine)
}

@BindingAdapter("exercise", "records", requireAll = true)
fun RecyclerView.adapterRecords(exercise: Exercise, records: List<Record>) {
    if (this.adapter == null)
        this.adapter = RecordsRecyclerViewAdapter(records, exercise)
    else
        (adapter as RecordsRecyclerViewAdapter).updateList(records)
}

@BindingAdapter("sModel", "exercise", "fragment", requireAll = false)
fun RecyclerView.adapterVideos(sModel: MainSharedViewModel, exercise: Exercise, fragment: ViewExerciseFragment) {
    if (this.adapter == null)
        this.adapter = VideosRecyclerViewAdapter(exercise.videos, exercise, sModel, fragment)
    else
        (adapter as VideosRecyclerViewAdapter).updateList(exercise.videos)
}

@BindingAdapter("sModel", "myRoutines", "fragment", requireAll = true)
fun RecyclerView.adapterMyRoutines(sModel: MainSharedViewModel, myRoutines: List<Routine>, fragment: MyRoutinesFragment) {
    if (this.adapter == null)
        this.adapter = MyRoutinesRecyclerViewAdapter(myRoutines, sModel, fragment)
    else
        (adapter as MyRoutinesRecyclerViewAdapter).updateList(myRoutines)
}