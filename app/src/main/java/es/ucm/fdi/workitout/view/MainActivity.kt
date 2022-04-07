package es.ucm.fdi.workitout.view

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class MainActivity : AppCompatActivity() {
    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //finish()

        /*val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()*/

        //setContentView(R.layout.activity_main)
    }

    //Se lanza el intent para elegir una imagen del almacenamiento interno
    fun selectImageFromGallery() = selectImageFromGalleryResultFlow.launch("image/*")

    //Al volver del intent se recupera el uri de la imagen elegida
    private val selectImageFromGalleryResultFlow =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { mainSharedViewModel.setTempImage(uri) }
        }
}