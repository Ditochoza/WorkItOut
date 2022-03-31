package es.ucm.fdi.workitout.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.viewModel.EditSharedViewModel

class EditActivity: AppCompatActivity() {
    private val editSharedViewModel: EditSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        editSharedViewModel.saveStateHandle()
        super.onPause()
    }
}