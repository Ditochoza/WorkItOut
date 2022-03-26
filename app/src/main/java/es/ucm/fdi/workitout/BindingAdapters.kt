package es.ucm.fdi.workitout

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.drawable.DrawableUtils

@BindingAdapter("muscles")
fun ChipGroup.adapterChipsMuscles(muscles: List<String>) {
    muscles.forEach {
        addView(Chip(context).apply {
            text = it
            isClickable = true
            isCheckable = true
            isSelectionRequired = true
            setChipDrawable(ChipDrawable.createFromAttributes(context,
                null,
                0,
                R.style.Widget_Material3_Chip_Filter))
        })
    }
}