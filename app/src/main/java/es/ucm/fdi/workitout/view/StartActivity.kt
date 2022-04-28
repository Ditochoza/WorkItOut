package es.ucm.fdi.workitout.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.color.DynamicColors
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.collectFlow
import es.ucm.fdi.workitout.utils.getNavController
import es.ucm.fdi.workitout.utils.setBackgroundDefault
import es.ucm.fdi.workitout.viewModel.StartSharedViewModel

class StartActivity : AppCompatActivity() {
    private val startSharedViewModel: StartSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //Lanzamos la SplashScreen
        installSplashScreen()
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)

        setContentView(R.layout.activity_start)

        //Aplicamos el color de fondo a la aplicación (Por problemas con DynamicColors)
        setBackgroundDefault(R.id.fc_start_activity)

        setupCollectors()
    }

    private fun setupCollectors() {
        //Collector para mostrar Toast cortos
        startSharedViewModel.shortToastRes.collectFlow(this) { resMessage ->
            Toast.makeText(this, getString(resMessage), Toast.LENGTH_SHORT).show()
        }

        //Collector para realizar navegación
        startSharedViewModel.navigateActionRes.collectFlow(this) { navActionRes ->
            if (navActionRes == 0) {
                onBackPressed()
            } else {
                val navController = supportFragmentManager.getNavController(R.id.fc_start_activity)
                navController?.navigate(navActionRes)
            }
        }

        //Collector para pasar al MainActivity al iniciar sesión o registrarse
        startSharedViewModel.login.collectFlow(this) { email ->
            launchMainActivity(email)
        }
    }

    private fun launchMainActivity(email: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(DbConstants.USER_EMAIL, email)
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