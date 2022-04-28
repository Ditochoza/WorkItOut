package es.ucm.fdi.workitout.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.ActivityMainBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.utils.*
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //Lanzamos la SplashScreen
        installSplashScreen().setKeepOnScreenCondition {mainSharedViewModel.loading.value}
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

                setContentView(binding.root)

                //Aplicamos el color de fondo a la aplicación (Por problemas con DynamicColors)
                setBackgroundDefault(R.id.fc_main_activity)

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
            } else if (navActionRes != -1) {
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

    fun onLongClickExercise (exercise: Exercise, navActionRes: Int):Boolean {
        if (exercise.idUser == mainSharedViewModel.user.value.email) {
            MaterialAlertDialogBuilder(this)
                .setItems(R.array.array_options_exercise_routine) { _, i ->
                    when (i) {
                        0 -> { //Editar  ejercicio
                            mainSharedViewModel.setAndNavigate(exercise, navActionRes)
                        }
                        1 -> { //Eliminar ejercicio
                            createAlertDialog(getString(R.string.delete_exercise, exercise.name),
                                message = R.string.delete_exercise_confirmation_message,
                                icon = R.drawable.ic_round_delete_outline_24,
                                ok = R.string.confirm to { mainSharedViewModel.deleteExercise(exercise) },
                                cancel = R.string.cancel to {}
                            ).show()
                        }
                    }
                }
                .setTitle(exercise.name).show()
            return true
        } else return false
    }

    //Se crea un diálogo para cuando salimos del entrenamiento
    private fun createDialogExitTraining() {
        this.createAlertDialog(R.string.exit_training, R.string.exit_training_message,
            icon = R.drawable.ic_round_exit_24,
            ok = R.string.confirm to {
                deleteNotification(mainSharedViewModel.selectedRoutine.value.requestRoutineIdNotification)
                mainSharedViewModel.setAndNavigate(Routine(), R.id.action_trainingExercisesFragment_to_homeFragment)
            },
            cancel = R.string.cancel to {}
        ).show()
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

    //Controlamos si se quiere salir de un entrenamiento
    override fun onBackPressed() {
        if (supportFragmentManager.currentFragment is TrainingExercisesFragment)
            createDialogExitTraining()
        else super.onBackPressed()
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        mainSharedViewModel.saveStateHandle()
        super.onPause()
    }
}