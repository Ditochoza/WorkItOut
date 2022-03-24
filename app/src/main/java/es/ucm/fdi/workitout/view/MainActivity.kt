package es.ucm.fdi.workitout.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.repository.DbConstants
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyIfAvailable(this)

        val emailIntent = intent.getStringExtra(DbConstants.USER_EMAIL)

        lifecycleScope.launch {
            val email =
                if (!emailIntent.isNullOrBlank()) emailIntent
                else mainSharedViewModel.getStringUserDataStore(DbConstants.USER_EMAIL)

            if (email.isEmpty()) { //No se ha iniciado sesión
                //Se lanza la pantalla de login (Y registro)
                launchStartActivity()
            } else { //Hay un usuario con sesión iniciada
                mainSharedViewModel.fetchAll(email)
                Collections.emptyList<String>()
                setContentView(R.layout.activity_main)

                setupCollectors()
            }
        }
    }

    private fun setupCollectors() {
        //Collector para mostrar Toast cortos
        mainSharedViewModel.shortToastRes.collectLatestFlow(this) { resMessage ->
            Toast.makeText(this, getString(resMessage), Toast.LENGTH_SHORT).show()
        }

        //Collector para realizar navegación
        /*mainSharedViewModel.navigateActionRes.collectLatestFlow(this) { navActionRes ->
            if (navActionRes == 0) {
                onBackPressed()
            } else {
                val navController = supportFragmentManager.getNavController(R.id.fc_main_activity)
                navController?.navigate(navActionRes)
            }
        }*/

        //Collector para pasar al MainActivity al iniciar sesión o registrarse
        mainSharedViewModel.logout.collectLatestFlow(this) {
            if (it) {
                Toast.makeText(this, getString(R.string.logging_out), Toast.LENGTH_SHORT).show()
                launchStartActivity()
            }
        }
    }

    private fun launchStartActivity() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        mainSharedViewModel.saveStateHandle()
        super.onPause()
    }
}