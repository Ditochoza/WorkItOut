package es.ucm.fdi.workitout.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.getNavController
import es.ucm.fdi.workitout.viewModel.StartSharedViewModel

class StartActivity : AppCompatActivity() {
    private val startSharedViewModel: StartSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        setupCollectors()
    }

    private fun setupCollectors() {
        //Collector para mostrar Toast cortos
        startSharedViewModel.shortToastRes.collectLatestFlow(this) { resMessage ->
            Toast.makeText(this, getString(resMessage), Toast.LENGTH_SHORT).show()
        }

        //Collector para realizar navegación
        startSharedViewModel.navigateActionRes.collectLatestFlow(this) { navActionRes ->
            if (navActionRes == 0) {
                onBackPressed()
            } else {
                val navController = supportFragmentManager.getNavController(R.id.fc_start_activity)
                navController?.navigate(navActionRes)
            }
        }

        //Collector para pasar al MainActivity al iniciar sesión o registrarse
        startSharedViewModel.login.collectLatestFlow(this) { email ->
            launchMainActivity(email)
        }
    }

    private fun launchMainActivity(email: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        Toast.makeText(this, getString(R.string.login_as, email), Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        startSharedViewModel.saveStateHandle()
        super.onPause()
    }
}