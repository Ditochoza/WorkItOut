package es.ucm.fdi.workitout.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Se obtiene el fragment actual
val FragmentManager.currentFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

//Se obtiene el navController para realizar la navegación
fun FragmentManager.getNavController(container: Int): NavController? {
    val navHostFragment = this.findFragmentById(container) as NavHostFragment?
    return navHostFragment?.navController
}

//Comprueba que se cumpla la condición de error y de ser así muestra el error en el TextInputLayout
fun TextInputLayout.tilError(conditionError: Boolean, resError: Int): Boolean {
    return if (conditionError) {
        this.error = context.getString(resError)
        true
    } else {
        this.error = ""
        false
    }
}

//Collector para SharedFlow
inline fun <T> Flow<T>.collectFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collect { onCollect(it) } } }

//Collector para StateFlow
inline fun <T> Flow<T>.collectLatestFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collectLatest { onCollect(it) } } }