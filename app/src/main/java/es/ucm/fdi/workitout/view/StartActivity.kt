package es.ucm.fdi.workitout.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.viewModel.StartSharedViewModel

class StartActivity : AppCompatActivity() {
    private val startSharedViewModel: StartSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        startSharedViewModel.saveStateHandle()
        super.onPause()
    }
}