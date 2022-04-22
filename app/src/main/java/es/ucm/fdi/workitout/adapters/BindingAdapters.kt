package es.ucm.fdi.workitout.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.model.Video
import es.ucm.fdi.workitout.utils.loadResource
import es.ucm.fdi.workitout.view.ExercisesFragment
import es.ucm.fdi.workitout.view.HomeFragment
import es.ucm.fdi.workitout.view.MyRoutinesFragment
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel
import es.ucm.fdi.workitout.viewModel.CreateRoutineViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

//BindingAdapter para definir el funcionamiento de los botones del menú de la barra de estado
@BindingAdapter("sModel", "navActionResToSettings")
fun MaterialToolbar.onClick(sModel: MainSharedViewModel, navActionResToSettings: Int) {
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

//BindingAdapter para colocar los músculos para seleccionar en el ChipGroup de CreateExercise
//También se le añade un listener para actualizar los músculos seleccionados
@BindingAdapter("muscles", "vModel", requireAll = true)
fun ChipGroup.adapterChipsMusclesSelect(muscles: Array<String>, vModel: CreateExerciseViewModel) {
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
        })
    }
    setOnCheckedStateChangeListener { _, checkedIds ->
        val newMuscles = ArrayList<String>()
        checkedIds.forEach { chipId -> newMuscles.add(findViewById<Chip>(chipId).text.toString()) }
        vModel.updateMuscles(newMuscles)
    }
}
//Para ViewExerciseFragment
@BindingAdapter("muscles")
fun ChipGroup.adapterChipsMuscles(muscles: List<String>) {
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

//BindingAdapter para mostrar los chips con los músculos de las rutinas
@BindingAdapter("exercises")
fun ChipGroup.adapterChipsExercises(exercises: List<Exercise>) {
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

@BindingAdapter("sModel", "exercises", "myExercises", "fragment", requireAll = true)
fun RecyclerView.adapterExercises(sModel: MainSharedViewModel, exercises: List<Exercise>, myExercises: List<Exercise>, fragment: ExercisesFragment) {
    if (this.adapter == null)
        this.adapter = ExercisesRecyclerViewAdapter(exercises + myExercises, sModel, fragment)
    else
        (adapter as ExercisesRecyclerViewAdapter).updateList(exercises + myExercises)
}

@BindingAdapter("sModel","videos","viewModel", requireAll = false)
fun RecyclerView.adapterVideos(sModel: MainSharedViewModel,videos: List<Video>,viewModel:CreateExerciseViewModel?) {

    var videoArrayList = ArrayList(videos)

    if (this.adapter == null)
        this.adapter = VideosRecyclerViewAdapter(videoArrayList, sModel,viewModel)
    else
        (adapter as VideosRecyclerViewAdapter).updateList(videoArrayList)
}

@BindingAdapter("sModel", "myRoutines", "fragment", requireAll = true)
fun RecyclerView.adapterMyRoutines(sModel: MainSharedViewModel, myRoutines: List<Routine>, fragment: MyRoutinesFragment) {
    if (this.adapter == null)
        this.adapter = MyRoutinesRecyclerViewAdapter(myRoutines, sModel, fragment)
    else
        (adapter as MyRoutinesRecyclerViewAdapter).updateList(myRoutines)
}