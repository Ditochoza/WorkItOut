package es.ucm.fdi.workitout.adapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.utils.loadResource
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel

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