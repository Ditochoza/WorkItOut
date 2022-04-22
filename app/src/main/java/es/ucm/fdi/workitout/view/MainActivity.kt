package es.ucm.fdi.workitout.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.ActivityMainBinding
import es.ucm.fdi.workitout.utils.DbConstants
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.utils.currentFragment
import es.ucm.fdi.workitout.utils.getNavController
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

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
                setContentView(binding.root)

                setupNavigationDrawerItemListener()
                setupCollectors()
            }
        }
    }

    private fun setupNavigationDrawerItemListener() {
        binding.mainNavigationDrawer.setCheckedItem(R.id.home_nav_drawer_menu_item)
        binding.mainNavigationDrawer.setNavigationItemSelectedListener { item ->
            lifecycleScope.launchWhenStarted {
                when (supportFragmentManager.currentFragment) {
                    is HomeFragment -> {
                        when (item.itemId) {
                            R.id.home_nav_drawer_menu_item ->
                                closeDrawer()
                            R.id.exercises_nav_drawer_menu_item ->
                                mainSharedViewModel.navigate(R.id.action_homeFragment_to_exercisesFragment)
                            R.id.routines_nav_drawer_menu_item ->
                                mainSharedViewModel.navigate(R.id.action_homeFragment_to_myRoutinesFragment)
                        }
                    }
                    is ExercisesFragment -> {
                        when (item.itemId) {
                            R.id.home_nav_drawer_menu_item ->
                                mainSharedViewModel.navigate(R.id.action_exercisesFragment_to_homeFragment)
                            R.id.exercises_nav_drawer_menu_item ->
                                closeDrawer()
                            R.id.routines_nav_drawer_menu_item ->
                                mainSharedViewModel.navigate(R.id.action_exercisesFragment_to_myRoutinesFragment)
                        }
                    }
                    is MyRoutinesFragment -> {
                        when (item.itemId) {
                            R.id.home_nav_drawer_menu_item ->
                                mainSharedViewModel.navigate(R.id.action_myRoutinesFragment_to_homeFragment)
                            R.id.exercises_nav_drawer_menu_item ->
                                mainSharedViewModel.navigate(R.id.action_myRoutinesFragment_to_exercisesFragment)
                            R.id.routines_nav_drawer_menu_item ->  {
                                closeDrawer()
                            }
                        }
                    }
                }
                delay(100L)
                closeDrawer()
            }
            true
        }
    }


    private fun setupCollectors() {
        //Collector para mostrar Toast cortos
        mainSharedViewModel.shortToastRes.collectLatestFlow(this) { resMessage ->
            Toast.makeText(this, getString(resMessage), Toast.LENGTH_SHORT).show()
        }

        //Collector para realizar navegación
        mainSharedViewModel.navigateActionRes.collectLatestFlow(this) { navActionRes ->
            if (navActionRes == 0) {
                onBackPressed()
            } else {
                val navController = supportFragmentManager.getNavController(binding.fcMainActivity.id)
                navController?.navigate(navActionRes)
            }
        }

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

    //Se lanza el intent para elegir una imagen del almacenamiento interno
    fun selectImageFromGallery() = selectImageFromGalleryResultFlow.launch("image/*")

    //Al volver del intent se recupera el uri de la imagen elegida
    private val selectImageFromGalleryResultFlow =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { mainSharedViewModel.setTempImage(uri) }
        }

    fun openDrawer() {
        binding.mainDrawerLayout.open()
    }

    private fun closeDrawer() {
        binding.mainDrawerLayout.close()
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        mainSharedViewModel.saveStateHandle()
        super.onPause()
    }
}