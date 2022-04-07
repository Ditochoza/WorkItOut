package es.ucm.fdi.workitout.adapters

import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.utils.loadResource
import es.ucm.fdi.workitout.viewModel.CreateRoutineViewModel

@BindingAdapter("weekDays","vModel","til", requireAll = true)
fun AutoCompleteTextView.adapterWeekDays(weekDays: Array<String>, vModel: CreateRoutineViewModel, til: TextInputLayout) {
    setAdapter(ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, weekDays))
    setOnItemClickListener { _, _, i, _ ->
        vModel.updateWeekDay(i)
        til.error = ""
    }
}

//BindingAdapter para colocar una imagen en el ImageView a partir de su URL (HTTP) o Uri de imagen elegida
@BindingAdapter("imageUrl", "tempImageUri")
fun ImageView.loadImage(imageUrl: String, tempImageUri: Uri) {
    if (tempImageUri != Uri.EMPTY) this.setImageURI(tempImageUri)
    else if (imageUrl.isNotEmpty()) this.loadResource(imageUrl)
}