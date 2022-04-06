package es.ucm.fdi.workitout.utils

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


//Se carga la imagen en el ImageView
fun ImageView.loadResource(resource: String?, diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL) {
    Glide.with(context).load(resource).diskCacheStrategy(diskCacheStrategy).into(this)
}

//Se obtiene el fragment actual
//val FragmentManager.currentFragment: Fragment?
//    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

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

val EditText.string: String
    get() = text.toString()

fun Context.createAlertDialog(title: Any? = null, message: Int? = null, icon: Int? = null,
                              ok: Pair<Int,()->Unit>, cancel: Pair<Int,()->Unit>? = null,
                              neutral: Pair<Int, ()->Unit>? = null): MaterialAlertDialogBuilder {
    val builder = MaterialAlertDialogBuilder(this)
        .setPositiveButton(getString(ok.first)) { _,_ -> ok.second() }

    title?.let {
        if (it is Int) builder.setTitle(getString(it))
        if (it is String) builder.setTitle(it)
    }
    message?.let { builder.setMessage(getString(it)) }
    icon?.let { builder.setIcon(it) }
    neutral?.let { builder.setNeutralButton(getString(it.first)) { _, _ -> it.second() } }
    cancel?.let { builder.setNegativeButton(getString(it.first)) { _, _ -> it.second() } }

    return builder
}

//Se devuelve el color del atributo
//@ColorInt
/*fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}*/

//Collector para SharedFlow
inline fun <T> Flow<T>.collectFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collect { onCollect(it) } } }

//Collector para StateFlow
inline fun <T> Flow<T>.collectLatestFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collectLatest { onCollect(it) } } }