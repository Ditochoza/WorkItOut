package es.ucm.fdi.workitout

import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@BindingAdapter("muscles")
fun ChipGroup.adapterChipsMuscles(muscles: List<String>) {
    muscles.forEach {
        addView(Chip(context).apply {
            text = it
            isClickable = false
        })
    }
}