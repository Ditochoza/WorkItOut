package es.ucm.fdi.workitout.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.utils.loadResource
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

//BindingAdapter para colocar una imagen en el ImageView a partir de su URL (HTTP) o Uri de imagen elegida
/*@BindingAdapter("imageUrl", "tempImageUri")
fun ImageView.loadImage(imageUrl: String, tempImageUri: Uri) {
    if (tempImageUri != Uri.EMPTY) this.setImageURI(tempImageUri)
    else if (imageUrl.isNotEmpty()) this.loadResource(imageUrl)
}*/

//BindingAdapter para colocar una imagen en el ImageView a partir de su URL (HTTP)
@BindingAdapter("imageUrl")
fun ImageView.loadImage(imageUrl: String) {
    if (imageUrl.isNotEmpty()) this.loadResource(imageUrl)
}

//BindingAdapter para mostrar los chips con los músculos de las rutinas
@BindingAdapter("muscles")
fun ChipGroup.adapterChipsMuscles(muscles: List<String>) {
    muscles.forEach {
        addView(Chip(context).apply {
            text = it
            isClickable = false
        })
    }
}