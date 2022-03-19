package es.ucm.fdi.workitout.viewModel

import android.app.Application
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.view.EditActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditSharedViewModel(application: Application,
                          private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val _exercise = MutableStateFlow(savedStateHandle.get(::exercise.name) ?: Exercise())
    val exercise: StateFlow<Exercise> = _exercise.asStateFlow()


    fun displayCategories(){


        // setup the alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.getApplication())

        // add a checkbox list

        val selectedoption= exercise.value.categories

        val checkedItems = BooleanArray(selectedoption.size)

        builder.setMultiChoiceItems(selectedoption, checkedItems,
            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                // user checked or unchecked a box
            })

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // user clicked OK
        })
        builder.setNegativeButton("Cancel", null)

        // create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun displayMuscles(){

        // setup the alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.getApplication())

        // add a checkbox list

        val selectedoption= exercise.value.muscles

        val checkedItems = BooleanArray(selectedoption.size)

        builder.setMultiChoiceItems(selectedoption, checkedItems,
            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                // user checked or unchecked a box
            })

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // user clicked OK
        })
        builder.setNegativeButton("Cancel", null)

        // create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun clearErrors(til: TextInputLayout) {
        til.error = ""
    }

    fun saveStateHandle() {
        savedStateHandle.set(::exercise.name, exercise.value)
    }
}