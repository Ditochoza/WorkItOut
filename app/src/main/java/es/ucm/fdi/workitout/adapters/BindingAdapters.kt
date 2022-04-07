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
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.loadResource
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

//BindingAdapter para definir el funcionamiento de los botones del menú de la barra de estado
@BindingAdapter("sModel")
fun MaterialToolbar.onClick(sModel: MainSharedViewModel) {
    this.menu.findItem(R.id.item_update_menu_main).setOnMenuItemClickListener {
        sModel.fetchAll()
        return@setOnMenuItemClickListener true
    }
    this.menu.findItem(R.id.item_logout_menu_main).setOnMenuItemClickListener {
        sModel.logout()
        return@setOnMenuItemClickListener true
    }
    this.menu.findItem(R.id.item_settings_menu_main).setOnMenuItemClickListener {
        //TODO Implementar navegación a Ajustes
        return@setOnMenuItemClickListener true
    }
}

//BindingAdapter para colocar los músculos para seleccionar en el ChipGroup de CreateExercise
//También se le añade un listener para actualizar los músculos seleccionados
@BindingAdapter("muscles", "vModel")
fun ChipGroup.adapterChipsMuscles(muscles: Array<String>, vModel: CreateExerciseViewModel) {
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
fun ChipGroup.adapterChipsMuscles(exercises: List<Exercise>) {
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
@BindingAdapter("routines", "sModel", "scheduled", requireAll = true)
fun RecyclerView.adapterRoutines(routines: List<Routine>, sModel: MainSharedViewModel, scheduled: Boolean) {
    val routinesArrayList = if (scheduled && routines.size > 1) { //Sólo las rutinas programadas (A partir de la 1)
        ArrayList(routines.subList(1, routines.size))
    } else { //Todas las rutinas
        ArrayList(routines)
    }

    if (this.adapter == null)
        this.adapter = RoutinesRecyclerViewAdapter(routinesArrayList, sModel)
    else
        (adapter as RoutinesRecyclerViewAdapter).updateList(routinesArrayList)
}

@BindingAdapter("weekDays","vModel","til", requireAll = true)
fun AutoCompleteTextView.adapterWeekDays(weekDays: Array<String>, vModel: CreateRoutineViewModel, til: TextInputLayout) {
    setAdapter(ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, weekDays))
    setOnItemClickListener { _, _, i, _ ->
        vModel.updateWeekDay(i)
        til.error = ""
    }
}